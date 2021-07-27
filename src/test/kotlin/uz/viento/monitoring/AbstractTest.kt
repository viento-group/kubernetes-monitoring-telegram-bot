package uz.viento.monitoring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
abstract class AbstractTest

@AutoConfigureMockMvc
abstract class AbstractWebTest : AbstractTest() {
    @Autowired
    protected lateinit var mockMvc: MockMvc
}