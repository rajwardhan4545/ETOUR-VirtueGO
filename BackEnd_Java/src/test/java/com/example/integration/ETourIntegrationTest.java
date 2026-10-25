package com.example.integration;

import com.example.dto.BookingCreateRequestDTO;
import com.example.dto.CustomerDTO;
import com.example.dto.LoginDTO;
import com.example.dto.PassengerDTO;
import com.example.entities.*;
import com.example.enums.AuthProvider;
import com.example.enums.CustomerRole;
import com.example.repositories.*;
import com.example.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.example.backend_java.BackEndJavaApplication.class)
@ActiveProfiles("test")
class ETourIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DepartureRepository departureRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    private String testEmail;
    private final String testPassword = "Password@123";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        testEmail = "user_" + System.currentTimeMillis() + "@etourtest.com";
    }

    @Test
    @DisplayName("IT-01: User registration via /api/auth/register returns 201 Created")
    void testRegister_Success() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setFirstName("Rajesh");
        dto.setLastName("Sharma");
        dto.setEmail(testEmail);
        dto.setPhone("9876543210");
        dto.setPassword(testPassword);
        dto.setAddress("123 MG Road, Pune");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.firstName").value("Rajesh"));
    }

    @Test
    @DisplayName("IT-02: User login via /api/auth/login returns valid JWT token")
    void testLogin_ReturnsJwt() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setFirstName("Amit");
        dto.setLastName("Patel");
        dto.setEmail(testEmail);
        dto.setPhone("9876543211");
        dto.setPassword(testPassword);
        dto.setAddress("456 Ring Road, Ahmedabad");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(testEmail);
        loginDTO.setPassword(testPassword);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = responseNode.get("token").asText();
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals(testEmail, jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("IT-03: Protected endpoint /api/Booking without token returns 401 Unauthorized")
    void testProtectedEndpoint_WithoutToken_ReturnsUnauthorizedOrForbidden() throws Exception {
        mockMvc.perform(get("/api/Booking"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("IT-04: Protected endpoint /api/Booking with valid JWT succeeds (200 OK)")
    void testProtectedEndpoint_WithValidToken_Succeeds() throws Exception {
        String token = jwtUtil.generateToken("auth_user@etourtest.com", "CUSTOMER");

        mockMvc.perform(get("/api/Booking")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IT-05: Complete Booking Flow - Create Booking, Add Passenger, Fetch Booking & Passengers")
    void testMainBookingFlow_PersistAndRetrieve() throws Exception {
        CustomerMaster customer = new CustomerMaster();
        customer.setFirstName("Sunil");
        customer.setLastName("Verma");
        customer.setEmail("sunil_" + System.currentTimeMillis() + "@etourtest.com");
        customer.setPhone("9123456780");
        customer.setPassword("Secret@123");
        customer.setCustomerRole(CustomerRole.CUSTOMER);
        customer.setAuthProvider(AuthProvider.LOCAL);
        customer.setProfileCompleted(true);
        CustomerMaster savedCustomer = customerRepository.save(customer);

        CategoryMaster cat = new CategoryMaster();
        cat.setCatCode("KRL");
        cat.setCategoryName("Kerala Backwaters");
        cat.setSubcatCode("^");
        cat.setJumpFlag(false);
        CategoryMaster savedCat = categoryRepository.save(cat);

        DepartureMaster dep = new DepartureMaster();
        dep.setCategory(savedCat);
        dep.setDepartDate(LocalDate.now().plusDays(10));
        dep.setEndDate(LocalDate.now().plusDays(15));
        dep.setNoOfDays(5);
        DepartureMaster savedDep = departureRepository.save(dep);

        TourMaster tour = new TourMaster();
        tour.setCategory(savedCat);
        tour.setDeparture(savedDep);
        TourMaster savedTour = tourRepository.save(tour);

        BookingStatusMaster status = new BookingStatusMaster();
        status.setStatusName("CONFIRMED");
        BookingStatusMaster savedStatus = bookingStatusRepository.save(status);

        String token = jwtUtil.generateToken(savedCustomer.getEmail(), "CUSTOMER");

        BookingCreateRequestDTO bookingReq = new BookingCreateRequestDTO();
        bookingReq.setCustomerId(savedCustomer.getId());
        bookingReq.setTourId(savedTour.getId());
        bookingReq.setNoOfPax(2);
        bookingReq.setTourAmount(new BigDecimal("25000.00"));
        bookingReq.setTaxes(new BigDecimal("2500.00"));
        bookingReq.setStatusId(savedStatus.getId());

        MvcResult bookingResult = mockMvc.perform(post("/api/Booking")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn();

        JsonNode bookingJson = objectMapper.readTree(bookingResult.getResponse().getContentAsString());
        Integer createdBookingId = bookingJson.get("bookingId").asInt();
        assertNotNull(createdBookingId);

        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setBookingId(createdBookingId);
        passengerDTO.setPaxName("Sunil Verma");
        passengerDTO.setPaxBirthdate(LocalDate.of(1990, 5, 15));
        passengerDTO.setPaxType("Adult");
        passengerDTO.setPaxAmount(new BigDecimal("12500.00"));

        mockMvc.perform(post("/api/passenger/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paxName").value("Sunil Verma"));

        mockMvc.perform(get("/api/passenger/booking/" + createdBookingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paxName").value("Sunil Verma"))
                .andExpect(jsonPath("$[0].bookingId").value(createdBookingId));

        mockMvc.perform(get("/api/Booking/" + createdBookingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(createdBookingId))
                .andExpect(jsonPath("$.noOfPax").value(2));
    }
}
