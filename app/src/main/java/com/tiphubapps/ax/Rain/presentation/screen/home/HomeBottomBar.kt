package com.tiphubapps.ax.Rain.presentation.screen.home

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tiphubapps.ax.Rain.ui.theme.AppThemeColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



@Composable
fun HomeBottomBar(onShowSend: () -> Unit,
               onShowReceive: () -> Unit,
                  onShowBuy: () -> Unit,
                  onShowProfile: () -> Unit,
                  onShowCamera: () -> Unit,
) {
    val context = LocalContext.current
    
    fun showSend(){
        onShowSend()
    }

    fun showReceive(){
        onShowReceive()
    }

    fun showCamera(){
        onShowCamera()
    }

    fun prepareBottomMenu(): List<BottomMenuItem> {
        val bottomMenuItemsList = arrayListOf<BottomMenuItem>()

        // add menu items
        bottomMenuItemsList.add(BottomMenuItem(label = "Receive", icon = Icons.Default.Favorite))
        bottomMenuItemsList.add(BottomMenuItem(label = "Buy", icon = Icons.Default.AccountBox))
        bottomMenuItemsList.add(BottomMenuItem(label = "Send", icon = Icons.Default.Add))
        bottomMenuItemsList.add(BottomMenuItem(label = "Profile", icon = Icons.Default.Home))

        return bottomMenuItemsList
    }

    val bottomMenuItemsList = prepareBottomMenu()
    var selectedItem by remember {
        mutableStateOf("Receive")
    }

    BottomAppBar(
        backgroundColor = MaterialTheme.colors.AppThemeColor,
        elevation = 0.dp,
        cutoutShape = CircleShape,
        content = {


            /*IconButton(onClick = { showSend() }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Receive",
                    tint = Color.Red
                )
            }*/

            BottomNavigationItem(
                selected = (false),
                onClick = {
                    showSend()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Receive"
                    )
                },
                enabled = true
            )

            BottomNavigationItem(
                selected = (false),
                onClick = {
                    onShowBuy()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Buy"
                    )
                },
                enabled = true
            )

            /*IconButton(onClick = { showCamera() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Favourite Icon",
                    tint = Color.Red
                )
            }*/

            BottomNavigationItem(
                selected = false,
                onClick = {},
                icon = {},
                enabled = false
            )

            /*IconButton(onClick = { showReceive() }) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Favourite Icon",
                    tint = Color.Red
                )
            }*/

            BottomNavigationItem(
                selected = (false),
                onClick = {
                    showReceive()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Receive"
                    )
                },
                enabled = true
            )

            BottomNavigationItem(
                selected = (false),
                onClick = {
                    onShowProfile()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Profile"
                    )
                },
                enabled = true
            )

        }
    )
}



data class BottomMenuItem(val label: String, val icon: ImageVector)

