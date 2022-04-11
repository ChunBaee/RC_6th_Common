package com.jcorp.rc_mission_6

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.jcorp.rc_mission_6.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.lang.Thread
import java.text.SimpleDateFormat
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener,
    DiceAdapter.LongClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<mViewModel>()
    private lateinit var adapter: DiceAdapter
    private lateinit var bossAnim: AnimationDrawable
    private lateinit var playerAnim: AnimationDrawable
    private lateinit var effectAnim: AnimationDrawable
    private lateinit var bulletAnim: Animation

    private lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        prefs = getSharedPreferences("SAVE", MODE_PRIVATE)

        binding.lifecycleOwner = this

        binding.rollDice.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnLoad.setOnClickListener(this)



        setRv()
        setPrefs()
        observe()

    }

    private fun setRv() {
        adapter = DiceAdapter(this)
        binding.rvDice.layoutManager = GridLayoutManager(this, 5)
        binding.rvDice.adapter = adapter

        for (i in 0 until 15) {
            viewModel.mDiceList.add(DiceData(i, 0, false, ""))
            viewModel.diceLocation.add(i)
        }
        viewModel.syncList()
        adapter.setItem(viewModel.mDiceList)
        adapter.longClickListener(this)

    }

    private fun setPrefs() {
        val pref = getSharedPreferences("Prefs", MODE_PRIVATE)
    }

    private fun addNewDice() {
        if (viewModel.diceNum.value!! < 15) {
            viewModel.calculateMoney()
            if (viewModel.isMoneyEnough.value == true) {
                makeDice()
            }
        }
    }

    private fun makeDice() {
        if (viewModel.diceNum.value!! < 15) {
            val rand = viewModel.diceLocation[Random.nextInt(viewModel.diceLocation.size)]
            Log.d("////", "makeDice: $rand")
            if (!viewModel.mDiceList[rand].isDice) {
                viewModel.diceLocation.remove(rand)
                setNewDice(rand, 0)
            }
        }
    }

    private fun combineDice(curLocation: Int, type: Int) {
        for (i in 0 until viewModel.mDiceList.size) {
            if (i != curLocation) {
                if (viewModel.mDiceList[i].level == viewModel.mDiceList[curLocation].level && viewModel.mDiceList[i].Type == viewModel.mDiceList[curLocation].Type) {
                    Log.d("////", "combineDice: /// $i ///")
                    viewModel.mDiceList[i] = DiceData(i, 0, false, "")
                    viewModel.diceNum.value = viewModel.diceNum.value!! - 2
                    viewModel.diceLocation.add(i)
                    when (type) {
                        0 -> viewModel.firePosition.remove(i)
                        1 -> viewModel.icePosition.remove(i)
                        2 -> viewModel.elecPosition.remove(i)
                        3 -> viewModel.healPosition.remove(i)
                    }
                    viewModel.setSkill(
                        viewModel.mDiceList[curLocation].Type,
                        viewModel.mDiceList[curLocation].level
                    )
                    setNewDice(curLocation, viewModel.mDiceList[curLocation].level)
                    break
                }
            }
        }
        Log.d("////", "combineDice: ${viewModel.mDiceList}")
        viewModel.syncList()
    }

    private fun setNewDice(position: Int, curLevel: Int) {
        val typeNum = Random.nextInt(4)
        when (typeNum) {
            0 -> viewModel.firePosition.add(position)
            1 -> viewModel.icePosition.add(position)
            2 -> viewModel.elecPosition.add(position)
            3 -> viewModel.healPosition.add(position)
        }
        Log.d("////", "setNewDice: $curLevel")
        var newDice = DiceData(position, curLevel + 1, true, Dices.DiceType[typeNum])
        viewModel.mDiceList[position] = newDice
        viewModel.syncList()
        viewModel.diceNum.value = viewModel.diceNum.value!! + 1
    }

    private fun observe() {
        viewModel.currentMoney.observe(this, Observer {
            binding.txtCur.text = it.toString()
        })
        viewModel.requireMoney.observe(this, Observer {
            binding.txtReq.text = it.toString()
        })
        viewModel.diceList.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })
        viewModel.bossState.observe(this, Observer {
            when (it) {
                true -> {
                    binding.bossAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.boss_idle, theme)
                }
                false -> {
                    binding.bossAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.boss_attack, theme)
                    viewModel.mPlayerHealth.value =
                        viewModel.mPlayerHealth.value!! - viewModel.mBossDamage
                }
            }
            bossAnim = binding.bossAnim.background as AnimationDrawable
            bossAnim.start()

        })
        viewModel.playerState.observe(this, Observer {
            when (it) {
                true -> binding.playerAnim.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.player_idle, theme)

                false -> {
                    binding.playerAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.player_attack, theme)
                    bulletAnim =
                        AnimationUtils.loadAnimation(applicationContext, R.anim.player_bullet)
                    binding.playerAttack.startAnimation(bulletAnim)
                    viewModel.mBossHealth.value =
                        viewModel.mBossHealth.value!! - viewModel.mPlayerDamage.value!!
                }

            }
            playerAnim = binding.playerAnim.background as AnimationDrawable
            playerAnim.start()
            viewModel.typeState.postValue(0)
        })

        viewModel.typeState.observe(this, Observer {
            when (it) {
                0 -> {
                    binding.effectAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.normal_effect, theme)
                }
                1 -> {
                    binding.effectAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.effect_fire, theme)
                }
                2 -> {
                    binding.effectAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.effect_ice, theme)
                }
                3 -> {
                    binding.effectAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.effect_elec, theme)
                }
                4 -> {
                    binding.effectAnim.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.effect_heal, theme)
                }
            }
            effectAnim = binding.effectAnim.background as AnimationDrawable
            effectAnim.start()
        })

        viewModel.mPlayerHealth.observe(this, Observer {
            binding.playerHealth.progress = it
        })

        viewModel.mBossHealth.observe(this, Observer {
            binding.bossHealth.progress = it
            when(it <= 0) {
                true -> {
                    bossAnim.stop()
                    var bossDie = ObjectAnimator.ofFloat(binding.bossAnim, "alpha", 0F)
                    bossDie.duration = 1000L
                    bossDie.start()
                }
            }
        })

        viewModel.deleteLoad.observe(this, Observer {
            prefs.edit().remove(it).commit()
        })
    }

    private fun increaseMoneyThread() {
        Thread {
            while (true) {
                Thread.sleep(2000)
                viewModel.addCurMoney()
            }
        }.start()
    }

    private fun bossMoveThread() {
        Thread {
            while (true) {
                viewModel.bossState.postValue(true)
                Thread.sleep(3000)
                viewModel.bossState.postValue(false)
                Thread.sleep(1000)
            }
        }.start()
    }

    private fun playerMoveThread() {
        Thread {
            while (true) {
                viewModel.playerState.postValue(true)
                Thread.sleep(2000)
                viewModel.playerState.postValue(false)
                Thread.sleep(1000)
            }
        }.start()
    }

    override fun onStart() {
        increaseMoneyThread()
        bossMoveThread()
        playerMoveThread()
        super.onStart()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.roll_dice -> {
                addNewDice()
            }

            R.id.btn_save -> {
                Thread.sleep(1000)

                val editor = prefs.edit()
                val curTime = SimpleDateFormat("yyyy년 MM월 dd일 - HH : mm").format(System.currentTimeMillis())

                val jsonObject = JSONObject()
                jsonObject.put("saveTime", curTime)
                jsonObject.put("playerHealth", viewModel.mPlayerHealth.value!!.toString())
                jsonObject.put("bossHealth", viewModel.mBossHealth.value!!.toString())
                jsonObject.put("currentMoney", viewModel.currentMoney.value!!.toString())
                jsonObject.put("requireMoney", viewModel.requireMoney.value!!.toString())

                /*val set = mutableSetOf<String>()
                set.add(viewModel.mPlayerHealth.value!!.toString())
                set.add(viewModel.mBossHealth.value!!.toString())
                set.add(viewModel.currentMoney.value!!.toString())
                set.add(viewModel.requireMoney.value!!.toString())*/

                editor.putString(curTime, jsonObject.toString())
                editor.commit()
                //저장 및 불러오기
            }

            R.id.btn_load -> {
                val list = mutableMapOf<String, Any>()
                for(i in prefs.all) {
                    list[i.key] = i.value!!
                    var strJson = i.value.toString()
                    try {
                        val response = JSONObject(strJson)
                        val loadData = LoadData(response.getString("saveTime"), response.getString("playerHealth"), response.getString("bossHealth"), response.getString("currentMoney"), response.getString("requireMoney"))
                        viewModel.mLoadDataList.add(loadData)
                    } catch (e : JSONException) {

                    }
                }
                viewModel.syncLoad()
                val dialog = LoadDialog()
                dialog.show(supportFragmentManager, "LoadDialog")
            }
        }
    }

    override fun onLongClick(view: View, position: Int) {
        if (viewModel.mDiceList[position].isDice) {
            when (viewModel.mDiceList[position].Type) {
                "Fire" -> {
                    if (viewModel.firePosition.size > 1) {
                        combineDice(position, 0)
                    }
                }
                "Ice" -> {
                    if (viewModel.icePosition.size > 1) {
                        combineDice(position, 1)
                    }
                }
                "Electric" -> {
                    if (viewModel.elecPosition.size > 1) {
                        combineDice(position, 2)
                    }
                }
                "Heal" -> {
                    if (viewModel.healPosition.size > 1) {
                        combineDice(position, 3)
                    }
                }
            }
        }
    }
}