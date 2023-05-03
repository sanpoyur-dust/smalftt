package org.yurusanp.smallfontt

// semantic domain satisfying:
// if t1 = t2 then eval(t1) = eval(t2)
// if t1 != t2 then eval(t1) != eval(t2)
// reification of equivalent values is unique
sealed interface Value {
  data class Pi(val vDom: Value, val cls: Cls) : Value
  data class Sg(val vDom: Value, val cls: Cls) : Value
  data class Lam(val cls: Cls) : Value
  data class Pair(val vFst: Value, val vSnd: Value) : Value
  data object Univ : Value

  // a neutral form can be wrapped to become a value
  data class Stuck(val neu: Neutral, val ty: Value) : Value
}

sealed interface Neutral {
  data class Var(val lv: Lv) : Neutral
  // TODO: why do we need to store the value of dom?
  data class App(val nLam: Neutral, val vArg: Value, val vDom: Value) : Neutral
  data class Fst(val nPair: Neutral) : Neutral
  data class Snd(val nPair: Neutral) : Neutral
}
