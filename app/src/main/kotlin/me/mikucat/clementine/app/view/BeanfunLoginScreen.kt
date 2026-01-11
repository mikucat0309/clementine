package me.mikucat.clementine.app.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import me.mikucat.clementine.BeanfunAccount
import me.mikucat.clementine.app.R
import me.mikucat.clementine.app.route.BeanfunLogin
import me.mikucat.clementine.app.theme.Theme
import me.mikucat.clementine.app.view.component.MessageDialog
import me.mikucat.clementine.app.viewmodel.BeanfunLoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BeanfunLoginScreen(
    key: BeanfunLogin,
    backStack: NavBackStack<NavKey>,
    vm: BeanfunLoginViewModel = koinViewModel(),
) {
    val success = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(vm.success) {
        vm.success.collect {
            success.value = it
        }
    }
    success.value?.let {
        MessageDialog(
            title = "Login Successful",
            text = "$it has successfully logged in.",
            onDismiss = {
                success.value = null
                backStack.removeLastOrNull()
            },
        )
    }

    val error = remember { mutableStateOf<Throwable?>(null) }
    LaunchedEffect(vm.error) {
        vm.error.collect {
            error.value = it
        }
    }
    error.value?.let {
        MessageDialog(
            title = it::class.simpleName ?: "Exception",
            text = it.message ?: "Unknown error",
            onDismiss = {
                error.value = null
                backStack.removeLastOrNull()
            },
        )
    }

    val accounts by vm.accounts.collectAsStateWithLifecycle()
    LaunchedEffect(key.token) {
        vm.fetchAccounts()
    }

    val isFetching by vm.isFetching.collectAsStateWithLifecycle()
    val isLogin by vm.isLogin.collectAsStateWithLifecycle()
    Screen(
        accounts = accounts,
        onClickAccount = { account -> vm.login(account, key.token) },
        isFetching,
        isLogin,
    )
}

private val emptyAccounts = listOf(BeanfunAccount("", "No Account"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    accounts: List<BeanfunAccount>,
    onClickAccount: (BeanfunAccount) -> Unit,
    isFetching: Boolean = false,
    isLogin: Boolean = false,
) {
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Beanfun Login") }) },
    ) { innerPadding ->
        Box(
            Modifier.padding(innerPadding),
        ) {
            if (isFetching) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                val onClickAccount: (BeanfunAccount) -> Unit =
                    if (accounts.isNotEmpty()) onClickAccount else { _ -> }
                val accounts = accounts.ifEmpty { emptyAccounts }
                AccountList(
                    accounts,
                    onClickAccount,
                    Modifier.padding(16.dp),
                    enabled = !isLogin,
                )
            }
        }
    }
}

@Composable
private fun AccountList(
    accounts: List<BeanfunAccount>,
    onClickAccount: (BeanfunAccount) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(accounts, key = { it.id }) { account ->
            AccountItem(
                account = account,
                onClick = {
                    onClickAccount(account)
                },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AccountItem(
    account: BeanfunAccount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    ListItem(
        headlineContent = {
            Text(account.nick)
        },
        supportingContent = {
            Text(account.id)
        },
        leadingContent = {
            Icon(
                painterResource(R.drawable.account_circle_24px),
                contentDescription = null,
                Modifier.size(40.dp),
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick,
            ),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    )
}

@Preview
@Composable
private fun PreviewScreen() {
    Theme(true) {
        val list = listOf(
            BeanfunAccount("account1", "Account 1"),
            BeanfunAccount("account2", "Account 2"),
            BeanfunAccount("account3", "Account 3"),
        )
        Screen(list, {})
    }
}
