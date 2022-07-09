package com.mobile.jettipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.jettipcalculator.components.InputField
import com.mobile.jettipcalculator.ui.theme.JetTipCalculatorTheme
import com.mobile.jettipcalculator.utils.calculateTotalPerPerson
import com.mobile.jettipcalculator.utils.calculateTotalTip
import com.mobile.jettipcalculator.widgets.RoundedIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipCalculatorTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Composable
@Preview
fun TopHeader(totalPerPerson: Double = 133.01) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(CornerSize(12.dp)),
        color = Color(0xFFE0DCE7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )

        }

    }
}

@ExperimentalComposeUiApi
@Composable
@Preview
fun MainContent() {
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)
    val totalPerPersonValue = remember {
        mutableStateOf(0.0)
    }

    Column(modifier = Modifier.padding(12.dp)) {
        BillForm(tipAmountState = tipAmountState,
            splitByState = splitByState,
            range = range,
            totalPersonState = totalPerPersonValue){

        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1 .. 100,
    splitByState : MutableState<Int>,
    tipAmountState : MutableState<Double>,
    totalPersonState : MutableState<Double>,
    onValChange: (String) -> Unit = {}) {

    val totalBillState = remember {
        mutableStateOf("")
    }
    val sliderPositionChange = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionChange.value * 100).toInt()


    val isValidBill = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current


    TopHeader(totalPersonState.value)

    Surface(
        modifier = modifier
            .padding(top = 10.dp, bottom = 2.dp, start = 2.dp, end = 2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp, color = Color.LightGray)) {
        Column {
            InputField(valueState = totalBillState,
                labelId = "Enter Bill Amount",
                enabled = true,
                isSingleLine = true,
                onActions = KeyboardActions {
                    if (!isValidBill) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })

            if(isValidBill){
                Row(modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split", modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 6.dp)
                        .align(alignment = CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End){
                        RoundedIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value = if(splitByState.value>1) splitByState.value-1 else 1
                                totalPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(),splitByState.value,tipPercentage)
                            }, elevation = 5.dp)
                        Text(text = "${splitByState.value}", modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .align(alignment = CenterVertically))
                        RoundedIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                      if(splitByState.value<range.last){
                                          splitByState.value = splitByState.value+1
                                      }
                                totalPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(),splitByState.value,tipPercentage)

                            }, elevation = 5.dp)
                    }
                }

                //Tip Row
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Tip", modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 6.dp)
                        .align(alignment = CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))

                    Text(text = "$$tipPercentage", modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 6.dp)
                        .align(alignment = CenterVertically))

                }

                Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally) {
                    Text(text = "$tipPercentage%",modifier.padding(top = 10.dp))
                    Spacer(modifier=Modifier.height(5.dp))
                    Slider(value = sliderPositionChange.value, onValueChange = {newVal ->
                        sliderPositionChange.value=newVal
                        tipAmountState.value = calculateTotalTip(totalBillState.value.toDouble(),tipPercentage)

                        totalPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(),splitByState.value,tipPercentage)

                    }, modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5)
                }
            }
            else{
                Box {
                }
            }
        }
    }
}



@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        MainContent()
    }
}