package co.imba.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import co.imba.ui.theme.*

enum class ExpenseCategory(
    val label: String,
    val icon: ImageVector,
    val color: Color
) {
    FOOD("Food", Icons.Rounded.LocalDining, CatFood),
    TAXI("Taxi", Icons.Rounded.DirectionsCar, CatTaxi),
    FUN("Fun", Icons.Rounded.SportsEsports, CatFun),
    SHOPPING("Shopping", Icons.Rounded.ShoppingBag, CatShopping),
    COFFEE("Coffee", Icons.Rounded.LocalCafe, CatCoffee),
    HOME("Home", Icons.Rounded.Home, CatHome),
    HEALTH("Health", Icons.Rounded.FavoriteBorder, CatHealth),
    OTHER("Other", Icons.Rounded.MoreHoriz, CatOther)
}
