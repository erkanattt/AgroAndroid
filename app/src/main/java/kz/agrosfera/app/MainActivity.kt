package kz.agrosfera.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.coroutines.launch
import kz.agrosfera.app.databinding.ActivityMainBinding
import kz.agrosfera.app.ui.auth.AuthNavArgs

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        val app = application as AgroApp
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.authRepository.session.collect { session ->
                    isLoggedIn = session != null
                }
            }
        }

        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_check -> if (!isLoggedIn) {
                    navController.navigate(
                        R.id.loginFragment,
                        bundleOf(
                            AuthNavArgs.REDIRECT_AI to true,
                            AuthNavArgs.REDIRECT_TAB to R.id.nav_check,
                        ),
                    )
                    return@setOnItemSelectedListener false
                }
                R.id.nav_chat -> if (!isLoggedIn) {
                    navController.navigate(
                        R.id.loginFragment,
                        bundleOf(
                            AuthNavArgs.REDIRECT_CHAT to true,
                            AuthNavArgs.REDIRECT_TAB to R.id.nav_chat,
                        ),
                    )
                    return@setOnItemSelectedListener false
                }
            }
            NavigationUI.onNavDestinationSelected(item, navController)
            true
        }
        binding.bottomNav.selectedItemId = R.id.nav_home

        val mainDestinations = setOf(
            R.id.nav_home,
            R.id.nav_check,
            R.id.nav_chat,
            R.id.nav_knowledge,
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id in mainDestinations
        }
    }

    fun selectTab(@IdRes menuItemId: Int) {
        if (menuItemId == R.id.nav_check && !isLoggedIn) {
            navigateToLogin(redirectAi = true, tab = R.id.nav_check)
            return
        }
        if (menuItemId == R.id.nav_chat && !isLoggedIn) {
            navigateToLogin(redirectChat = true, tab = R.id.nav_chat)
            return
        }
        if (menuItemId == R.id.nav_profile) {
            val navHost =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHost.navController.navigate(R.id.nav_profile)
            return
        }
        val item = binding.bottomNav.menu.findItem(menuItemId) ?: return
        binding.bottomNav.selectedItemId = item.itemId
    }

    fun navigateToLogin(redirectAi: Boolean = false, redirectChat: Boolean = false, tab: Int = 0) {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHost.navController.navigate(
            R.id.loginFragment,
            bundleOf(
                AuthNavArgs.REDIRECT_AI to redirectAi,
                AuthNavArgs.REDIRECT_CHAT to redirectChat,
                AuthNavArgs.REDIRECT_TAB to tab,
            ),
        )
    }

    fun isUserLoggedIn(): Boolean = isLoggedIn
}
