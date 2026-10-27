package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.ui.theme.StatusMAM
import com.example.ui.theme.StatusNormal
import com.example.ui.viewmodel.PoshanViewModel

@Composable
fun AuthScreen(
    viewModel: PoshanViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.appLanguage.collectAsState()
    val adminUser by viewModel.adminUsernameInput.collectAsState()
    val adminPin by viewModel.adminPinInput.collectAsState()
    val parentMobile by viewModel.parentMobileInput.collectAsState()
    val parentCode by viewModel.parentRegCodeInput.collectAsState()

    var selectedTab by remember { mutableIntStateOf(1) } // Default to Parent portal
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Trilingual Switcher at the top of Auth Screen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "भाषा / Language:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate600
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AppLanguage.values().forEach { lang ->
                    FilterChip(
                        selected = language == lang,
                        onClick = { viewModel.setLanguage(lang) },
                        label = {
                            Text(
                                text = lang.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (language == lang) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryIndigoContainer,
                            selectedLabelColor = PrimaryIndigo
                        ),
                        modifier = Modifier.testTag("lang_chip_${lang.code}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Branding (Clean Material 3 Minimalism)
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(PrimaryIndigoContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FamilyRestroom,
                contentDescription = "Parent Connect",
                tint = PrimaryIndigo,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = AppStrings.appTitle(language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900,
            textAlign = TextAlign.Center
        )

        Text(
            text = AppStrings.appSubtitle(language),
            fontSize = 13.sp,
            color = Slate600,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )

        Text(
            text = "AW-Center-042 (Shivaji Nagar)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryIndigo,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dual Segmented Tabs (Clean Material 3)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100),
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PrimaryIndigo,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = AppStrings.tabAdmin(language),
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_auth_admin")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = AppStrings.tabParent(language),
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_auth_parent")
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedTab == 0) {
            // ADMIN LOGIN FORM
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(16.dp))
                    .testTag("admin_login_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.tabAdmin(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Slate900
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = adminUser,
                        onValueChange = { viewModel.setAdminUsername(it) },
                        label = { Text(AppStrings.adminUsername(language)) },
                        placeholder = { Text("admin_aw042") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_username_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminPin,
                        onValueChange = { viewModel.setAdminPin(it) },
                        label = { Text(AppStrings.adminPassword(language)) },
                        placeholder = { Text("123456") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            viewModel.loginAdmin()
                        }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.loginAdmin()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text(
                            text = AppStrings.btnLoginAdmin(language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-Click Demo Fill for Admin
                    OutlinedButton(
                        onClick = { viewModel.fillAdminDemo() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("demo_admin_fill_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "⚡ Quick Demo: Rekha Tai Patil (admin_aw042 / 123456)",
                            fontSize = 12.sp,
                            color = PrimaryIndigo
                        )
                    }
                }
            }
        } else {
            // PARENT / GUARDIAN ACTIVATION & LOGIN FORM
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(16.dp))
                    .testTag("parent_login_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Parent Secure",
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.parentLoginTitle(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900
                        )
                    }

                    Text(
                        text = if (language == AppLanguage.MARATHI)
                            "अंगणवाडीतून दिलेला नोंदणीकृत मोबाईल नंबर व कोड प्रविष्ट करा."
                        else if (language == AppLanguage.HINDI)
                            "आंगनवाड़ी से प्राप्त पंजीकृत मोबाइल नंबर एवं कोड दर्ज करें।"
                        else
                            "Enter the registered mobile & single-use code issued by Anganwadi.",
                        fontSize = 12.sp,
                        color = Slate600,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = parentMobile,
                        onValueChange = { viewModel.setParentMobile(it) },
                        label = { Text(AppStrings.parentMobileHint(language)) },
                        placeholder = { Text("9890123456") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_mobile_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = parentCode,
                        onValueChange = { viewModel.setParentRegCode(it.uppercase()) },
                        label = { Text(AppStrings.parentRegCodeHint(language)) },
                        placeholder = { Text("ACT-7842-AG or AW-042-01") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Reg Code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            viewModel.loginParent()
                        }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_regcode_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.loginParent()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("parent_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text(
                            text = AppStrings.btnVerifyParent(language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Embedded Pre-Loaded Linked Children & Guardian Profiles (PRD v1.2)
            Text(
                text = AppStrings.quickDemoTitle(language),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Child 1: Aarav Sachin Gaikwad (Normal - Green)
            DemoParentAccountCard(
                childId = "AW-042-01",
                childName = "Aarav Sachin Gaikwad",
                guardian = "Sachin Gaikwad",
                mobile = "9890123456",
                code = "ACT-7842-AG",
                status = "Normal (Healthy)",
                statusColor = StatusNormal,
                statusBg = Color(0xFFECFDF5),
                nextEvent = "Ration Distribution (24-Oct)",
                onClick = { viewModel.fillParentDemo("9890123456", "ACT-7842-AG") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Child 2: Ananya Rahul Meshram (MAM - Amber)
            DemoParentAccountCard(
                childId = "AW-042-02",
                childName = "Ananya Rahul Meshram",
                guardian = "Snehal Meshram",
                mobile = "9765432109",
                code = "ACT-9912-AM",
                status = "MAM (Moderate)",
                statusColor = StatusMAM,
                statusBg = Color(0xFFFFFBEB),
                nextEvent = "Nutrition Counseling",
                onClick = { viewModel.fillParentDemo("9765432109", "ACT-9912-AM") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Child 3: Vihaan Pramod Shinde (Normal - Green)
            DemoParentAccountCard(
                childId = "AW-042-03",
                childName = "Vihaan Pramod Shinde",
                guardian = "Pramod Shinde",
                mobile = "9422187654",
                code = "ACT-3301-VS",
                status = "Normal (Healthy)",
                statusColor = StatusNormal,
                statusBg = Color(0xFFECFDF5),
                nextEvent = "All doses complete",
                onClick = { viewModel.fillParentDemo("9422187654", "ACT-3301-VS") }
            )
        }
    }
}

@Composable
private fun DemoParentAccountCard(
    childId: String,
    childName: String,
    guardian: String,
    mobile: String,
    code: String,
    status: String,
    statusColor: Color,
    statusBg: Color,
    nextEvent: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("demo_chip_$childId"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = childName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = status,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Guardian: $guardian • 📞 $mobile",
                    fontSize = 11.sp,
                    color = Slate600
                )
                Text(
                    text = "Code: $code | Event: $nextEvent",
                    fontSize = 11.sp,
                    color = PrimaryIndigo,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryIndigoContainer)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Login ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )
            }
        }
    }
}
