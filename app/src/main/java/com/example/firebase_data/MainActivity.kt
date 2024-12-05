package com.example.firebase_data

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebase_data.data_class.Student
import com.example.firebase_data.ui.theme.Firebase_dataTheme
import com.google.firebase.database.*

class MainActivity : ComponentActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("students_data")

        setContent {
            Firebase_dataTheme {
                var studentData by remember { mutableStateOf("") }
                var studentName by remember { mutableStateOf("") }
                      var fatherName by remember { mutableStateOf("") }
                var rollNo by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val builder = StringBuilder()
                            for (studentSnapshot in snapshot.children) {
                                val name = studentSnapshot.key ?: "Unknown"
                                val student = studentSnapshot.getValue(Student::class.java)
                                student?.let {
                                    builder.append("$name: Father - ${it.father_name}, Roll No - ${it.roll_no}\n")
                                }
                            }
                            studentData = builder.toString().trim()
                        }

                            override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebasfeEfrrors", error.message)
                        }
                    })
                }


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        Text(text = if (studentData.isEmpty()) "Loading data....." else studentData)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Add New Students dta", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        BasicTextField(
                            value = studentName,
                            onValueChange = { studentName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                              decorationBox = { innerTextField ->
                                Box(Modifier.padding(8.dp)) {
                                    if (studentName.isEmpty()) Text("Student Name")
                                    innerTextField()
                                }
                            }
                          )


                                BasicTextField(
                            value = fatherName,
                            onValueChange = { fatherName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            decorationBox = { innerTextField ->
                                Box(Modifier.padding(8.dp)) {
                                    if (fatherName.isEmpty()) Text("Father's Name")
                                    innerTextField()
                                }
                            }
                        )


                        BasicTextField(
                            value = rollNo,
                               onValueChange = { rollNo = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            decorationBox = { innerTextField ->
                                Box(Modifier.padding(8.dp)) {
                                    if (rollNo.isEmpty()) Text("Roll Number")
                                    innerTextField()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))


                                      Button(onClick = {
                            if (studentName.isNotBlank() && fatherName.isNotBlank() && rollNo.isNotBlank()) {
                                val newStudent = Student(father_name = fatherName, roll_no = rollNo.toInt())
                                reference.child(studentName).setValue(newStudent)
                                   studentName = ""
                                fatherName = ""
                                rollNo = ""
                            }
                        }) {
                            Text("Add Student")
                        }
                    }
                }
            }
        }
    }
}
