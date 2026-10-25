package com.yourname.stulogplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourname.stulogplus.data.local.Subject
import com.yourname.stulogplus.data.local.Task
import com.yourname.stulogplus.data.repository.StudyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun today() = SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN).format(Date())
private val subjectColors = listOf(Color(0xFFA78BFA), Color(0xFF60A5FA), Color(0xFFF472B6), Color(0xFF6EE7B7), Color(0xFFFBBF24), Color(0xFFFB7185))

@Composable private fun ScreenColumn(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp), content = {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        content()
    })
}
@Composable private fun AddDialog(title: String, label: String, onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = {
        OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text(label) }, singleLine = true)
    }, confirmButton = { TextButton(onClick = { if (value.isNotBlank()) onAdd(value.trim()) }, enabled = value.isNotBlank()) { Text("追加") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } })
}

@Composable fun TimerScreen(repo: StudyRepository) {
    var elapsed by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val tasks by repo.tasks(today()).collectAsStateWithLifecycle(initialValue = emptyList())
    LaunchedEffect(running) { while (running) { delay(1000); elapsed++ } }
    ScreenColumn("集中タイム") {
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(248.dp).background(Color(0xFF8B5CF6), CircleShape), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%02d:%02d".format(elapsed / 60, elapsed % 60), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(if (running) "集中しています" else "自分のペースで", color = Color.White.copy(alpha = .85f))
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { running = !running }, modifier = Modifier.weight(1f)) { Icon(if (running) Icons.Default.Pause else Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text(if (running) "一時停止" else "スタート") }
            OutlinedButton(onClick = { running = false; scope.launch { repo.addRecord(elapsed, today()); elapsed = 0 } }, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Check, null); Spacer(Modifier.width(4.dp)); Text("記録") }
            IconButton(onClick = { running = false; elapsed = 0 }) { Icon(Icons.Default.Refresh, "リセット") }
        }
        Spacer(Modifier.height(24.dp))
        Text("今日のタスク", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text("${tasks.count { it.isCompleted }} / ${tasks.size} 件完了", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        if (tasks.isEmpty()) Text("やることタブから今日のタスクを追加しよう ✨", color = MaterialTheme.colorScheme.onSurfaceVariant)
        else tasks.take(4).forEach { Text((if (it.isCompleted) "✓ " else "○ ") + it.title, modifier = Modifier.padding(vertical = 4.dp)) }
    }
}

@Composable fun TasksScreen(repo: StudyRepository) {
    val tasks by repo.tasks(today()).collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }
    ScreenColumn("今日のやること") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Button(onClick = { showAdd = true }) { Icon(Icons.Default.Add, null); Text("追加") } }
        if (tasks.isEmpty()) Text("やることを追加しましょう ✨", modifier = Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().clickable { scope.launch { repo.updateTask(task.copy(isCompleted = !task.isCompleted)) } }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle, null, tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(task.title, Modifier.weight(1f).padding(start = 12.dp), fontWeight = FontWeight.Medium)
                        IconButton(onClick = { scope.launch { repo.deleteTask(task) } }) { Icon(Icons.Default.DeleteOutline, "削除") }
                    }
                }
            }
        }
    }
    if (showAdd) AddDialog("やることを追加", "タイトル", { showAdd = false }) { text -> scope.launch { repo.addTask(text, today()); showAdd = false } }
}

@Composable fun SubjectsScreen(repo: StudyRepository) {
    val subjects by repo.subjects().collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }
    ScreenColumn("教科・教材") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Button(onClick = { showAdd = true }) { Icon(Icons.Default.Add, null); Text("教科を追加") } }
        if (subjects.isEmpty()) Text("教科を追加して、学習を整理しよう 📚", modifier = Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(subjects, key = { it.id }) { subject ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).background(Color(subject.color), CircleShape), contentAlignment = Alignment.Center) { Text(subject.name.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
                        Text(subject.name, Modifier.weight(1f).padding(start = 14.dp), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
    if (showAdd) AddDialog("教科を追加", "教科名", { showAdd = false }) { name ->
        val color = subjectColors[subjects.size % subjectColors.size].value.toLong().toInt()
        scope.launch { repo.addSubject(name, color); showAdd = false }
    }
}

@Composable fun RecordsScreen(repo: StudyRepository) {
    val records by repo.records(today()).collectAsStateWithLifecycle(initialValue = emptyList())
    val total = records.sumOf { it.actualTimeSeconds }
    ScreenColumn("学習記録") {
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Column(Modifier.padding(22.dp)) {
                Text("今日の学習時間", color = MaterialTheme.colorScheme.onPrimary)
                Text("${total / 3600}時間 ${(total % 3600) / 60}分", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                Text("${records.size} 回の記録", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .85f))
            }
        }
        Spacer(Modifier.height(16.dp))
        if (records.isEmpty()) Text("集中タイムで学習を記録すると、ここに表示されます 📖", color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(records, key = { it.id }) { record ->
                Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.primary)
                    Text("集中タイム", Modifier.weight(1f).padding(start = 12.dp))
                    Text("${record.actualTimeSeconds / 60}分", fontWeight = FontWeight.SemiBold)
                } }
            }
        }
    }
}

@Composable fun SettingsScreen(repo: StudyRepository) {
    var confirm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    ScreenColumn("設定") {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("StulogPlus", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Version 1.0.0 · オフラインで保存", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().clickable { confirm = true }.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("すべてのデータを消去", color = MaterialTheme.colorScheme.error)
                        Text("教科・タスク・学習記録を削除します", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
    if (confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text("データをすべて削除しますか？") },
        text = { Text("この操作は取り消せません。") },
        confirmButton = { TextButton(onClick = { scope.launch { repo.clearAll(); confirm = false } }) { Text("削除", color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("キャンセル") } })
}
