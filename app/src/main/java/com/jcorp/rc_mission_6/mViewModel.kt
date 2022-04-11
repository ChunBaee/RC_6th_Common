package com.jcorp.rc_mission_6

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class mViewModel : ViewModel() {

    var mLoadDataList = mutableListOf<LoadData>()
    val deleteLoad = MutableLiveData<String>()

    var mDiceList = mutableListOf<DiceData>()
    var isMoneyEnough = MutableLiveData<Boolean>(true)
    var bossState = MutableLiveData<Boolean>(true) // true -> 일반(3초) , false -> 공격(1초)
    var playerState = MutableLiveData<Boolean>(true) //true -> 일반(2초) , false -> 공
    var typeState = MutableLiveData<Int>(0) //0:불 1:얼음 2:전기 3:회복
    var diceLocation = mutableListOf<Int>()
    var diceNum = MutableLiveData<Int>(0) //생성된 주사위 갯수

    var firePosition = mutableListOf<Int>()
    var icePosition = mutableListOf<Int>()
    var elecPosition = mutableListOf<Int>()
    var healPosition = mutableListOf<Int>()

    var mBossHealth = MutableLiveData<Int>(10000)
    var mBossDamage = 20
    var mPlayerHealth = MutableLiveData<Int>(1000)
    var mPlayerDamage = MutableLiveData<Int>(3000)

    private val _diceList = MutableLiveData<MutableList<DiceData>>()
    val diceList : LiveData<MutableList<DiceData>> = _diceList

    val _currentMoney = MutableLiveData<Int>(100)
    val currentMoney : LiveData<Int> = _currentMoney

    val _requireMoney = MutableLiveData<Int>(10)
    val requireMoney : LiveData<Int> = _requireMoney

    private val _loadDataList = MutableLiveData<MutableList<LoadData>>()
    val loadDataList : LiveData<MutableList<LoadData>> = _loadDataList

    fun addCurMoney() {
        _currentMoney.postValue(_currentMoney.value!! + 1000)
    }

    fun calculateMoney() {
        if(currentMoney.value!! >= requireMoney.value!!) {
            _currentMoney.postValue(_currentMoney.value!! - _requireMoney.value!!)
            _requireMoney.postValue(_requireMoney.value!! + 10)
            isMoneyEnough.postValue(true)
        } else {
            isMoneyEnough.postValue(false)
        }
    }

    fun setSkill(type : String, level : Int) {
        when(type) {
            "Fire" -> {
                mBossHealth.postValue(mBossHealth.value?.minus((100 * level)))
                typeState.postValue(1)
            }

            "Ice" -> {
                mBossHealth.postValue(mBossHealth.value?.minus((100 * level)))
                typeState.postValue(2)
            }

            "Electric" -> {
                mBossHealth.postValue(mBossHealth.value?.minus((100 * level)))
                typeState.postValue(3)
            }

            "Heal" -> {
                mPlayerHealth.postValue(mPlayerHealth.value?.plus(20 * level))
                typeState.postValue(4)
            }
        }
    }

    fun syncList() {
        _diceList.postValue(mDiceList)
    }

    fun syncLoad() {
        _loadDataList.postValue(mLoadDataList)
    }
}