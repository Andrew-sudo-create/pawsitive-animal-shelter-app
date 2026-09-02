package com.example.pawsitive_app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.StateFlow

val PrimaryRed = Color(0xFFE53935)
val CharcoalGray = Color(0xFF4A4A4A)
val SoftGreen = Color(0xFFE8F5E9)
val DarkGreen = Color(0xFF1B5E20)
val LightGray = Color(0xFFF5F5F5)

// --- MOCK DATA (used as fallback in Repository) ---
enum class NewsType { SPOTLIGHT, URGENT_APPEAL, EVENT }
data class NewsPost(
    val id: String,
    val type: NewsType,
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String,
    val meta: String = "",
    val tags: List<String> = emptyList(),
    val amountRaised: Int = 0,
    val goal: Int = 0
)

val mockNews = listOf(
    NewsPost("1", NewsType.SPOTLIGHT, "Dog of the Week", "Max is a sweet and energetic boy who loves fetch.", "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=600&auto=format&fit=crop", "12 Oct 2023", meta = "Max • 3 Yrs • Labrador", tags = listOf("Friendly", "House-trained")),
    NewsPost("2", NewsType.URGENT_APPEAL, "Emergency Medical Fund Needed", "Help us pay for Bella's emergency surgery after she was rescued.", "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?q=80&w=600&auto=format&fit=crop", "10 Oct 2023", amountRaised = 8500, goal = 15000),
    NewsPost("3", NewsType.EVENT, "Adoption Drive at the Park", "Join us this weekend to meet our lovely dogs looking for homes.", "https://images.unsplash.com/photo-1601758228041-f3b279ce7be6?q=80&w=600&auto=format&fit=crop", "15 Oct 2023", meta = "Central Park • 10:00 AM • Free Entry")
)

data class Dog(val id: String, val name: String, val breed: String, val age: String, val sex: String, val description: String, val imageUrls: List<String>, var isFavorite: Boolean = false)

val mockDogs = listOf(
    Dog("1", "Buddy", "Golden Retriever", "2 Years", "Male", "A very happy and energetic dog. Loves to play fetch and go for long walks. Needs a house with a big yard.", listOf("https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop")),
    Dog("2", "Bella", "German Shepherd", "1 Year", "Female", "Loyal and protective. She is still a puppy and needs some training but is very smart.", listOf("https://images.unsplash.com/photo-1589924691995-400dc9ecc119?q=80&w=600&auto=format&fit=crop")),
    Dog("3", "Charlie", "Mixed Breed", "3 Years", "Male", "A calm and affectionate boy who loves cuddling on the couch. Great with kids.", listOf("https://images.unsplash.com/photo-1517849845537-4d257902454a?q=80&w=600&auto=format&fit=crop"))
)

// --- NEWS SCREEN ---
@Composable
fun NewsScreen(viewModel: ShelterViewModel) {
    val newsList by viewModel.newsState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(LightGray)) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth().background(CharcoalGray, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pawsitive Shelter", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Centurion's Trusted Animal Rescue Centre", color = Color.LightGray, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Community News Board", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = { viewModel.fetchData() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimaryRed)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading && newsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryRed)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(newsList) { post ->
                        NewsCard(post)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(post: NewsPost) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Label Bar
            Row(
                modifier = Modifier.fillMaxWidth().background(
                    if (post.type == NewsType.EVENT) CharcoalGray else PrimaryRed
                ).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = when(post.type) {
                    NewsType.SPOTLIGHT -> Icons.Default.Star
                    NewsType.URGENT_APPEAL -> Icons.Default.Warning
                    NewsType.EVENT -> Icons.Default.Event
                }
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(post.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            // Image
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                if (post.meta.isNotEmpty()) {
                    Text(post.meta, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(post.description, color = CharcoalGray, fontSize = 14.sp)
                
                if (post.type == NewsType.URGENT_APPEAL) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val progress = if (post.goal > 0) post.amountRaised.toFloat() / post.goal else 0f
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = PrimaryRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("R${post.amountRaised} raised / R${post.goal} goal", fontSize = 12.sp, color = CharcoalGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed), modifier = Modifier.fillMaxWidth(), shape = CircleShape) {
                        Text("Donate Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                
                if (post.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        post.tags.forEach { tag ->
                            Box(modifier = Modifier.background(SoftGreen, CircleShape).padding(horizontal = 12.dp, vertical = 4.dp)) {
                                Text(tag, color = DarkGreen, fontSize = 12.sp)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.date, color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- ADOPTION SCREEN ---
@Composable
fun AdoptionScreen(viewModel: ShelterViewModel) {
    val dogsList by viewModel.dogsState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var currentIndex by remember { mutableStateOf(0) }
    
    if (isLoading && dogsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryRed)
        }
        return
    }
    
    if (dogsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No dogs available right now.")
        }
        return
    }

    // Safeguard index bounds when data reloads
    val safeIndex = currentIndex.coerceIn(0, (dogsList.size - 1).coerceAtLeast(0))
    if (safeIndex != currentIndex) {
        currentIndex = safeIndex
    }
    
    val currentDog = dogsList[safeIndex]
    
    Column(modifier = Modifier.fillMaxSize().background(LightGray)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Adoption Gallery", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("${dogsList.size} dogs looking for homes", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Carousel
            Box(modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(16.dp))) {
                Image(
                    painter = rememberAsyncImagePainter(currentDog.imageUrls.firstOrNull() ?: ""),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp).align(Alignment.TopEnd)) {
                    Text("${safeIndex + 1} / ${dogsList.size}", color = Color.White, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = { if (safeIndex > 0) currentIndex-- }, modifier = Modifier.background(Color.White.copy(alpha=0.7f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                    }
                    IconButton(onClick = { if (safeIndex < dogsList.size - 1) currentIndex++ }, modifier = Modifier.background(Color.White.copy(alpha=0.7f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(currentDog.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("${currentDog.breed} • ${currentDog.age} • ${currentDog.sex}", color = CharcoalGray)
                }
                IconButton(onClick = { viewModel.toggleFavorite(currentDog.id) }) {
                    Icon(if(currentDog.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = PrimaryRed)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(currentDog.description, color = CharcoalGray)
            
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().background(SoftGreen, RoundedCornerShape(8.dp)).padding(12.dp)) {
                Text("Interested in adopting ${currentDog.name}? Contact us to schedule a meet and greet!", color = DarkGreen)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed), modifier = Modifier.fillMaxWidth(), shape = CircleShape) {
                Text("Inquire About ${currentDog.name}", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dogsList.indices.toList()) { index ->
                    val dog = dogsList[index]
                    Image(
                        painter = rememberAsyncImagePainter(dog.imageUrls.firstOrNull() ?: ""),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                2.dp,
                                if (safeIndex == index) PrimaryRed else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { currentIndex = index }
                    )
                }
            }
        }
    }
}

// --- DONATE SCREEN ---
@Composable
fun DonateScreen() {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize().background(LightGray)) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().background(CharcoalGray, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Make a Difference", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Every contribution helps us save more lives", color = Color.LightGray, fontSize = 14.sp)
            }
        }
        
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quick Donate", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DonatePresetButton("R50", "Feeds 1 dog", modifier = Modifier.weight(1f))
                            DonatePresetButton("R150", "Feeds 3 dogs", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DonatePresetButton("R500", "Medical care", modifier = Modifier.weight(1f))
                            DonatePresetButton("R1000", "Sponsor a dog", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = CircleShape) {
                            Text("Choose Custom Amount", color = PrimaryRed)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Other Payment Methods", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        PaymentMethodRow(Icons.Default.QrCode, "SnapScan", "Scan to pay") { }
                        PaymentMethodRow(Icons.Default.CreditCard, "Card Payment", "Pay via PayFast") { 
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://payfast.co.za")))
                        }
                        PaymentMethodRow(Icons.Default.AccountBalance, "Bank Transfer", "View EFT Details") { }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(modifier = Modifier.fillMaxWidth().background(SoftGreen, RoundedCornerShape(8.dp)).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = DarkGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Secure & Transparent", color = DarkGreen, fontWeight = FontWeight.Bold)
                            Text("Donations are securely processed. Expense reports are published monthly.", color = DarkGreen, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DonatePresetButton(amount: String, impact: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = LightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amount, color = PrimaryRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(impact, color = CharcoalGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun PaymentMethodRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryRed)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Color.Gray)
    }
}

// --- MORE SCREEN ---
@Composable
fun MoreScreen() {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize().background(LightGray), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("About & Contact", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CharcoalGray)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Our Mission", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("To rescue, rehabilitate and rehome dogs in need while educating the community on responsible pet ownership.", color = Color.LightGray)
                }
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("500+", "Dogs Rescued", modifier = Modifier.weight(1f))
                StatCard("350+", "Adoptions", modifier = Modifier.weight(1f))
                StatCard("10", "Years Active", modifier = Modifier.weight(1f))
            }
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Contact Us", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactRow(Icons.Default.Phone, "Phone", "012 345 6789")
                    ContactRow(Icons.Default.Email, "Email", "info@pawsitiveshelter.co.za")
                    ContactRow(Icons.Default.LocationOn, "Address", "123 Rescue Lane, Centurion")
                }
            }
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Operating Hours", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    HoursRow("Monday - Friday", "09:00 - 16:00")
                    HoursRow("Saturday", "09:00 - 13:00")
                    HoursRow("Sunday & Public Holidays", "Closed")
                }
            }
        }
        
        item {
            Button(
                onClick = { 
                    val gmmIntentUri = Uri.parse("geo:0,0?q=123+Rescue+Lane,+Centurion")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            ) {
                Text("Open in Google Maps", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(number, color = PrimaryRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(label, color = CharcoalGray, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = CharcoalGray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HoursRow(day: String, hours: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(day, color = CharcoalGray)
        Text(hours, fontWeight = FontWeight.Bold)
    }
}
