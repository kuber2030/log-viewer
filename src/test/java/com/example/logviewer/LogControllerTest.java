//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.example.kuberneteslogviewer.controller.LogController;
//import com.example.kuberneteslogviewer.service.LogService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//@WebMvcTest(LogController.class)
//public class LogControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private LogService logService;
//
//    @BeforeEach
//    public void setUp() {
//        logService = mock(LogService.class);
//    }
//
//    @Test
//    public void testGetLogs() throws Exception {
//        mockMvc.perform(get("/logs")
//                .param("project", "test-project")
//                .param("environment", "production")
//                .param("time", "2023-01-01T00:00:00Z")
//                .param("threadId", "1234")
//                .param("searchText", "error")
//                .param("page", "0")
//                .param("size", "10")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testSwitchProject() throws Exception {
//        mockMvc.perform(post("/logs/switchProject")
//                .param("projectName", "new-project")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetLogsWithPagination() throws Exception {
//        mockMvc.perform(get("/logs")
//                .param("project", "test-project")
//                .param("environment", "testing")
//                .param("page", "1")
//                .param("size", "5")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//}