package com.outlook.apricus.uk.t5quickshipmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun fnCalculate(view: View) {
        var errorNote = ""

        errorNote += validateTonnage()
        errorNote += validateJump()
        errorNote += validateManeuver()

        val tonnage = editTonnage.text.toString().toInt()
        val jump = editJump.text.toString().toInt()
        val maneuver = editManeuver.text.toString().toInt()

        var forScaling = tonnage
        if (tonnage < 100){
            // small craft adjustment
            forScaling -= 5
        }

        // start
        var crew = 1
        var free = 1000
        var cost = 32.0

        // streamlining
        if (chkStreamlined.isChecked){
            cost += 62.0
        }

        // engineering
        when (jump) {
            0 -> {
                when (maneuver) {
                    0 -> { crew = 1; free = 995; cost += 1.0; }
                    1 -> { crew = 2; free = 946; cost += 56.0; }
                    2 -> { crew = 3; free = 897; cost += 111.0;}
                    3 -> { crew = 4; free = 848; cost += 166.0;}
                    4 -> { crew = 6; free = 795; cost += 221.0;}
                    5 -> { crew = 7; free = 746; cost += 276.0;}
                    6 -> { crew = 8; free = 697; cost += 331.0;}
                }
            }
            1 -> {
                when (maneuver){
                    0 -> { crew = 5; free = 824; cost += 46.0; }
                    1 -> { crew = 5; free = 804; cost += 86.0; }
                    2 -> { crew = 7; free = 751; cost += 141.0;}
                    3 -> { crew = 8; free = 702; cost += 196.0;}
                    4 -> { crew = 9; free = 653; cost += 251.0;}
                    5 -> { crew = 11; free = 599; cost += 308.0;}
                    6 -> { crew = 12; free = 550; cost += 363.0;}
                }
            }
            2 -> {
                when (maneuver){
                    0 -> { crew = 9; free = 658; cost += 86.0; }
                    1 -> { crew = 10; free = 634; cost += 127.0; }
                    2 -> { crew = 10; free = 614; cost += 167.0;}
                    3 -> { crew = 12; free = 560; cost += 223.0;}
                    4 -> { crew = 13; free = 511; cost += 278.0;}
                    5 -> { crew = 14; free = 462; cost += 333.0;}
                    6 -> { crew = 16; free = 409; cost += 388.0;}
                }
            }
            3 -> {
                when (maneuver){
                    0 -> { crew = 14; free = 487; cost += 128.0; }
                    1 -> { crew = 14; free = 467; cost += 168.0; }
                    2 -> { crew = 15; free = 443; cost += 208.0;}
                    3 -> { crew = 15; free = 423; cost += 248.0;}
                    4 -> { crew = 17; free = 370; cost += 303.0;}
                    5 -> { crew = 18; free = 321; cost += 358.0;}
                    6 -> { crew = 19; free = 272; cost += 413.0;}
                }
            }
            4 -> {
                when (maneuver){
                    0 -> { crew = 18; free = 321; cost += 168.0; }
                    1 -> { crew = 18; free = 301; cost += 208.0; }
                    2 -> { crew = 19; free = 277; cost += 248.0;}
                    3 -> { crew = 20; free = 253; cost += 289.0;}
                    4 -> { crew = 20; free = 233; cost += 329.0;}
                    5 -> { crew = 21; free = 184; cost += 384.0;}
                    6 -> { crew = 23; free = 131; cost += 439.0;}
                }
            }
            5 -> {
                when (maneuver){
                    0 -> { crew = 22; free = 155; cost += 209.0; }
                    1 -> { crew = 23; free = 131; cost += 249.0; }
                    2 -> { crew = 23; free = 111; cost += 289.0;}
                    3 -> { crew = 24; free = 87; cost += 329.0;}
                    4 -> { crew = 24; free = 67; cost += 369.0;}
                    5 -> { crew = 25; free = 43; cost += 409.0;}
                }
            }
        }

        // bridge
        if (chkBridge.isChecked) {
            if (free < 50) {
                errorNote += "Insufficient remaining tonnage for Bridge, deselecting\n"
                chkBridge.isChecked = false
            } else {
                free -= 50
            }
        }

        //emplacements
        if (tonnage < 100) {
            if (spinEmplacements.selectedItemId > 0L){
                free -= 3
                cost += 3
            }
        } else {
            when (spinEmplacements.selectedItemId) {
                0L -> { /*none nothing to do*/
                }
                1L -> {
                    free -= 10; cost += 2
                }
                2L -> {
                    free -= 10; cost += 5
                }
                3L -> {
                    free -= 10; cost += 10
                }
            }
        }
        val emplacementString = emplacementsString(spinEmplacements.selectedItemId, tonnage)

        // cargo and passengers
        var passengers = 0.0
        var cargo = 0.0
        when (spinCargoRatio.selectedItemId) {
            0L -> { cargo = free.toDouble(); passengers = 0.0 }
            1L -> { cargo = 0.8 * free; passengers = 0.05 * free }
            2L -> { cargo = 0.5 * free; passengers = 0.125 * free }
            3L -> { cargo = 0.2 * free; passengers = 0.2 * free }
            4L -> { cargo = 0.0; passengers = 0.25 * free }
        }

        // passenger accommodations
        when (spinAccommodations.selectedItemId) {
            0L -> { passengers *= 2 }
            1L -> { /* no change */ }
            2L -> { passengers *= 0.5 }
        }

        // volume adjustment
        val ratio = forScaling/1000.0
        var adjustedCrew = (crew * ratio).toInt()
        if (adjustedCrew < 1){
            adjustedCrew = 1
        }

        var adjustedBridge = 0.0
        if (chkBridge.isChecked) {
            adjustedBridge = 50.0 * ratio
        }

        val adjustedCargo = cargo * ratio
        val adjustedPassengers = (passengers * ratio).toInt()

        val adjustedCost = cost * ratio

        if (errorNote.isNotEmpty()) {
            editOutput.setText(errorNote)
        } else {
            var outputText = ""
            outputText += "Volume : $tonnage Tons\n"
            outputText += "Jump : $jump\n"
            outputText += "Maneuver : $maneuver\n"
            outputText += "Crew : $adjustedCrew\n"
            outputText += "Bridge : %.2f Tons\n".format(adjustedBridge)
            outputText += "Hardpoints : $emplacementString\n"
            outputText += "Passengers : $adjustedPassengers\n"
            outputText += "Cargo hold : %.2f Tons\n".format(adjustedCargo)
            outputText += "Cost : %.2f MCr".format(adjustedCost)
            editOutput.setText(outputText)
        }
    }

    private fun emplacementsString( index: Long, tons: Int): String{
        return when (tons){
            in 0..35 -> {
                if ( index > 0){
                    "One firmpoint emplacement"
                } else {
                    "No firmpoint emplacement"
                }
            }
            in 36..70 -> {
                if ( index > 0){
                    "Two firmpoint emplacements"
                } else {
                    "No firmpoint emplacements"
                }
            }
            in 71..99 -> {
                if ( index > 0){
                    "Three firmpoint emplacements"
                } else {
                    "No firmpoint emplacements"
                }
            }
            else -> {
                val hardpoints = tons / 100
                when(index) {
                    1L -> {
                        if (hardpoints > 1) {
                            "$hardpoints hardpoint single emplacements"
                        } else {
                            "One hardpoint single emplacement"
                        }
                    }
                    2L -> {
                        if (hardpoints > 1) {
                            "$hardpoints hardpoint double emplacements"
                        } else {
                            "One hardpoint double emplacement"
                        }
                    }
                    3L -> {
                        if (hardpoints > 1) {
                            "$hardpoints hardpoint triple emplacements"
                        } else {
                            "One hardpoint triple emplacement"
                        }
                    }
                    else -> {
                        if (hardpoints > 1) {
                            "No hardpoint emplacments"
                        } else {
                            "No hardpoint emplacement"
                        }
                    }
                }
            }
        }
    }

    private fun validateTonnage(): String{
        var errorNote = ""
        val maxTonnage = 5000
        val inTonnage = editTonnage.text.toString().toInt()
        val outTonnage = if (inTonnage > 99){
            if (inTonnage > maxTonnage){
                errorNote += "input tonnage greater than maximum allowed; tonnage adjusted\n"
                maxTonnage
            } else {
                if (inTonnage % 100 != 0) {
                    errorNote += "input tonnage rounded down\n"
                    100 * (inTonnage.div(100))
                } else {
                    inTonnage
                }
            }
        } else {
            if (inTonnage < 10){
                errorNote += "input tonnage lower than minimum allowed; tonnage adjusted\n"
                10
            } else {
                if (inTonnage % 10 != 0) {
                    errorNote += "input tonnage rounded down\n"
                    10 * (inTonnage.div( 10))
                } else {
                    inTonnage
                }
            }
        }
        editTonnage.setText(outTonnage.toString())
        return errorNote
    }

    private fun validateJump():String{
        var errorNote = ""
        val inTonnage = editTonnage.text.toString().toInt()
        val inJump = editJump.text.toString().toInt()

        val outJump = if (inJump > 5){
            if (inTonnage < 100) {
                errorNote += "small craft (tonnage < 100) cannot jump; jump adjusted\n"
                0
            } else {
                errorNote += "input jump greater than maximum allowed; jump adjusted\n"
                5
            }
        } else {
            if (inJump < 0){
                errorNote += "input jump lower than minimum allowed; jump adjusted\n"
                0
            } else {
                if (inJump > 0) {
                    if (inTonnage < 100) {
                        errorNote += "small craft (tonnage < 100) cannot jump; jump adjusted\n"
                        0
                    } else {
                        inJump
                    }
                } else {
                    inJump
                }
            }
        }
        editJump.setText(outJump.toString())
        return errorNote
    }

    private fun validateManeuver(): String{
        var errorNote = ""
        val inManeuver = editManeuver.text.toString().toInt()
        val inJump = editJump.text.toString().toInt()
        val outManeuver = if (inManeuver < 0){
            errorNote += "input maneuver lower than minimum allowed; maneuver adjusted\n"
            0
        } else {
            if (inJump > 4){
                if (inManeuver > 5){
                    errorNote += "input maneuver greater than maximum allowed where jump is 5; maneuver adjusted\n"
                    5
                } else {
                    inManeuver
                }
            } else {
                if (inManeuver > 6){
                    errorNote += "input maneuver greater than maximum allowed; maneuver adjusted\n"
                    6
                } else {
                    inManeuver
                }
            }
        }
        editManeuver.setText(outManeuver.toString())
        return errorNote
    }
}