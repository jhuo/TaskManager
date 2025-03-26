import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jhuo.taskmanager.auth.domain.repository.AuthRepository
import com.jhuo.taskmanager.auth.presentation.AuthUiEvent
import com.jhuo.taskmanager.auth.presentation.AuthViewModel
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_INVALID_EMAIL_ERROR
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_INVALID_PASSWORD_ERROR
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: AuthViewModel

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `test email input updates state`() = runTest {
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail("test@example.com"))
        assertEquals("test@example.com", viewModel.state.drop(1).first().email)
    }

    @Test
    fun `test password input updates state`() = runTest {
        viewModel.onEvent(AuthUiEvent.Input.EnterPassword("password123"))
        assertEquals("password123", viewModel.state.drop(1).first().password)
    }

    @Test
    fun `test invalid email shows error`() = runTest {
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail("invalid-email"))
        viewModel.onEvent(AuthUiEvent.ButtonClick.SignIn)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(AUTH_INVALID_EMAIL_ERROR, viewModel.state.drop(1).first().emailError)
    }

    @Test
    fun `test invalid password shows error`() = runTest {
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail("test@example.com"))
        viewModel.onEvent(AuthUiEvent.Input.EnterPassword("123"))
        viewModel.onEvent(AuthUiEvent.ButtonClick.SignIn)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(AUTH_INVALID_PASSWORD_ERROR, viewModel.state.drop(1).first().passwordError)
    }
}
