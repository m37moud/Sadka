package com.example.sadkaquran.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sadkaquran.data.models.Shikh

@Composable
fun ShikhMenuDropDown(
    modifier: Modifier = Modifier,
    name: String,
    shiks: List<Shikh>,
    menuItemSelected: (Shikh?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(shiks[0].name) }

    var dropDownWidth by remember { mutableStateOf(0) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.ArrowDropDown


    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = modifier
                .onSizeChanged {
                    dropDownWidth = it.width
                },
            readOnly = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            label = { Text(name) },
//            trailingIcon = {
//                Icon(icon, "contentDescription", Modifier.clickable { expanded = !expanded })
//            },
            trailingIcon = {
                IconButton(onClick = {
                    expanded = !expanded
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (expanded) "show less" else "show more"
                    )
                }
            },
        )
        val stateVertical = rememberScrollState(0)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .height(200.dp)
                    .width(
                        with(LocalDensity.current) {
                            dropDownWidth.toDp()
                        })
            ) {
//                DropdownMenuItem(onClick = {
//                    selectedText = "Select Shikh"
//                    menuItemSelected(null)
//                    expanded = false
//
//                }) {
//                    Text(text = "Select Shikh")
//                }
                shiks.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selectedText = label.name
                        menuItemSelected(label)
                        expanded = false

                    }) {
                        Text(text = label.name)
                    }
                }
            }
        }
}


