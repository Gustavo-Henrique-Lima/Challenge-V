package com.gustavonascimento.usersreader.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavonascimento.usersreader.entities.Role;
import com.gustavonascimento.usersreader.entities.User;
import com.gustavonascimento.usersreader.entities.dto.UploadReportDTO;
import com.gustavonascimento.usersreader.entities.dto.UserDTO;
import com.gustavonascimento.usersreader.entities.dto.UserUploadDTO;
import com.gustavonascimento.usersreader.factories.UserFactory;
import com.gustavonascimento.usersreader.repositories.RoleRepository;
import com.gustavonascimento.usersreader.repositories.UserRepository;
import com.gustavonascimento.usersreader.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ObjectMapper mapper;

    private Pageable pageable;

    @BeforeEach
    void init() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void searchShouldReturnMappedPageOfUserDTO() {
        Role admin = new Role(1L, "ROLE_ADMIN");
        Role operator = new Role(2L, "ROLE_OPERATOR");

        User u1 = UserFactory.createUser("Alice", "alice@example.com", true, admin);
        User u2 = UserFactory.createUser("Bob", "bob@example.com", false, operator);

        Page<User> page = new PageImpl<>(List.of(u1, u2), pageable, 2);
        Mockito.when(userRepository.search(eq("a"), eq("ROLE_ADMIN"), eq(true), eq(pageable)))
                .thenReturn(page);

        Page<UserDTO> result = service.search("a", "ROLE_ADMIN", true, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("Alice", result.getContent().get(0).getName());
        Assertions.assertEquals("bob@example.com", result.getContent().get(1).getEmail());
        Mockito.verify(userRepository, Mockito.times(1))
                .search("a", "ROLE_ADMIN", true, pageable);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        User u = UserFactory.createUser("Carol", "carol@example.com", true);
        u.setId(10L);

        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        UserDTO dto = service.findById(10L);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertEquals("Carol", dto.getName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(10L);
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
        Mockito.verify(userRepository, Mockito.times(1)).findById(999L);
    }

    @Test
    void uploadUsersFromFileShouldReturnErrorWhenFileIsEmpty() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(true);

        UploadReportDTO report = service.uploadUsersFromFile(file);

        Assertions.assertNotNull(report);
        Assertions.assertEquals(0, report.inserted);
        Assertions.assertEquals(0, report.skipped);
        Assertions.assertFalse(report.errors.isEmpty());
        Mockito.verifyNoInteractions(mapper, userRepository, roleRepository);
    }

    @Test
    void uploadUsersFromFileShouldInsertNewSkipExistingAndReportMissingRole() throws Exception {
        UserUploadDTO rNew = new UserUploadDTO();
        rNew.name = "Alice";
        rNew.email = "alice@example.com";
        rNew.role = "ROLE_ADMIN";
        rNew.isActive = true;
        rNew.createdAt = "2025-01-01T12:00:00Z";

        UserUploadDTO rExisting = new UserUploadDTO();
        rExisting.name = "Bob";
        rExisting.email = "bob@example.com";
        rExisting.role = "ROLE_OPERATOR";
        rExisting.isActive = false;
        rExisting.createdAt = "2025-01-02T10:00:00Z";

        UserUploadDTO rMissingRole = new UserUploadDTO();
        rMissingRole.name = "Carol";
        rMissingRole.email = "carol@example.com";
        rMissingRole.role = "ROLE_UNKNOWN";
        rMissingRole.isActive = true;

        UserUploadDTO rInvalid = new UserUploadDTO();
        rInvalid.name = "No Email";
        rInvalid.email = "   ";
        rInvalid.role = "ROLE_ADMIN";

        UserUploadDTO rBlankCreatedAt = new UserUploadDTO();
        rBlankCreatedAt.name = "Dave";
        rBlankCreatedAt.email = "dave@example.com";
        rBlankCreatedAt.role = "ROLE_OPERATOR";
        rBlankCreatedAt.isActive = null;
        rBlankCreatedAt.createdAt = "";

        List<UserUploadDTO> payload = List.of(rNew, rExisting, rMissingRole, rInvalid, rBlankCreatedAt);

        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getInputStream()).thenReturn(dummyStream());

        Mockito.when(mapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(payload);

        Mockito.when(userRepository.findByEmailIgnoreCase("alice@example.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmailIgnoreCase("bob@example.com")).thenReturn(Optional.of(UserFactory.createUser("Bob", "bob@example.com", false)));
        Mockito.when(userRepository.findByEmailIgnoreCase("carol@example.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmailIgnoreCase("dave@example.com")).thenReturn(Optional.empty());

        Role roleAdmin = new Role(1L, "ROLE_ADMIN");
        Role roleOperator = new Role(2L, "ROLE_OPERATOR");
        Mockito.when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        Mockito.when(roleRepository.findByAuthority("ROLE_OPERATOR")).thenReturn(Optional.of(roleOperator));
        Mockito.when(roleRepository.findByAuthority("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UploadReportDTO report = service.uploadUsersFromFile(file);

        Assertions.assertEquals(2, report.inserted);
        Assertions.assertEquals(3, report.skipped);

        Mockito.verify(userRepository, Mockito.times(2)).save(userCaptor.capture());
        List<User> saved = userCaptor.getAllValues();

        User savedAlice = saved.stream().filter(u -> "alice@example.com".equals(u.getEmail())).findFirst().orElse(null);
        Assertions.assertNotNull(savedAlice);
        Assertions.assertEquals("Alice", savedAlice.getName());
        Assertions.assertTrue(savedAlice.isActive());
        Assertions.assertTrue(savedAlice.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getAuthority())));
        Assertions.assertEquals(LocalDateTime.ofInstant(
                Instant.parse("2025-01-01T12:00:00Z"), ZoneOffset.UTC), savedAlice.getCreatedAt());

        User savedDave = saved.stream().filter(u -> "dave@example.com".equals(u.getEmail())).findFirst().orElse(null);
        Assertions.assertNotNull(savedDave);
        Assertions.assertEquals("Dave", savedDave.getName());
        Assertions.assertFalse(savedDave.isActive());
        Assertions.assertTrue(savedDave.getRoles().stream().anyMatch(r -> "ROLE_OPERATOR".equals(r.getAuthority())));
        Assertions.assertNotNull(savedDave.getCreatedAt());
    }

    @Test
    void uploadUsersFromFileShouldReportGeneralFailureWhenMapperThrows() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getInputStream()).thenReturn(dummyStream());

        Mockito.when(mapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenThrow(new RuntimeException("boom"));

        UploadReportDTO report = service.uploadUsersFromFile(file);

        Assertions.assertEquals(0, report.inserted);
        Assertions.assertEquals(0, report.skipped);
        Assertions.assertFalse(report.errors.isEmpty());
    }

    private static InputStream dummyStream() {
        return new ByteArrayInputStream("[]".getBytes());
    }
}
