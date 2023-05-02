package org.yurusanp.smallfontt

// semantic domain
sealed interface Value {
  data class Pi(val vDom: Value, val cls: Cls) : Value
  data class Sg(val vDom: Value, val cls: Cls) : Value
  data class Lam(val cls: Cls) : Value
  data class Pair(val vFst: Value, val vSnd: Value) : Value
  data object Univ : Value
  data class Stuck(val neu: Neutral, val ty: Value) : Value
}

sealed interface Neutral {
  data class Var(val lv: Lv) : Neutral
  data class App(val nLam: Neutral, val vArg: Value, val vDom: Value) : Neutral
  data class Fst(val pair: Neutral) : Neutral
  data class Snd(val pair: Neutral) : Neutral
}
