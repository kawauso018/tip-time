package com.aonimu.tiptime

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.aonimu.tiptime.databinding.ActivityMainBinding
import java.text.NumberFormat
import kotlin.math.cos

class MainActivity : AppCompatActivity() {

    //バインディングオブジェクトのクラス内の最上位変数を宣言
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activity_main.xmlのViewsへのアクセスに使用するバインディングオブジェクトの初期化
        binding = ActivityMainBinding.inflate(layoutInflater)
        //R.layout.activity_mainを渡す代わりに、アプリのビュー階層のルートbinding.rootを指定
        setContentView(binding.root)

        //[Calculate]ボタンにクリックリスナーを設定
        binding.calculateButton.setOnClickListener {
            //チップを計算する
            calculateTip()
        }

        //テキストボックスにおいてキーの押下が発生したときにトリガーされるリスナー
        binding.costOfServiceEditText.setOnKeyListener {
            //viewと押されたキーを表すコード、キーイベントを渡す
            //キーイベントは使わないので_
                view, keyCode, _ ->
            handleKeyEvent(view, keyCode)
        }
    }

    private fun calculateTip() {
        //costOfServiceEditTextのtextを取得（String型ではなくEditable型）
        //EditableをStringに変換
        val stringInTextField = binding.costOfServiceEditText.text.toString()
        //Doubleに変換(stringInTextFieldがnullの可能性がある)
        val cost = stringInTextField.toDoubleOrNull()
        //nullの時は何もしない
        //値を消して再度計算しても表示されたままにならないようにする
        if (cost == null) {
            binding.tipResult.text = ""
            return
        }
        //RadioGroupのcheckedRadioButtonId属性を取得（何を選んだか）
        val selectedID = binding.tipOptions.checkedRadioButtonId
        //ラジオボタンごとにチップの割合を設定
        val tipPercentage = when (selectedID) {
            R.id.option_twenty_percent -> 0.20
            R.id.option_eighteen_percent -> 0.18
            else -> 0.15
        }
        //チップ計算
        var tip = cost * tipPercentage
        //SwitchのisChecked属性を取得（端数を切り上げるかどうか）
        val roundUp = binding.roundUpSwitch.isChecked
        //切り上げするかどうか
        if (roundUp) {
            //importせずに直接ceil()関数を使用している
            tip = kotlin.math.ceil(tip)
        }
        //数値を通過として書式設定する
        val formatTip = NumberFormat.getCurrencyInstance().format(tip)
        //チップの金額をtipResultのtext属性に反映
        //android:text="@string/tip_amount"を消したのになぜ反映できるのか？
        binding.tipResult.text = getString(R.string.tip_amount, formatTip)
    }

    //Enter キーでキーボードを非表示にする
    //keyCode 入力パラメータが KeyEvent.KEYCODE_ENTER と等しい場合に画面キーボードを非表示にする非公開のヘルパー関数
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        //キーイベントが処理された場合は true を返し、処理されなかった場合は false を返します。
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            //InputMethodManager は、ソフト キーボードの表示と非表示を制御
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}