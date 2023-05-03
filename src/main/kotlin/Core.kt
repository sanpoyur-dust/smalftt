package org.yurusanp.smallfontt

// well-typed term
sealed interface Term {
  // variable
  data class Var(val ix: Ix) : Term

  // dependent function (product) type, Pi type
  // Π (x : dom) . b[x]
  data class Pi(val dom: Term, val fam: Fam) : Term

  // dependent pair (co-product) type, Sigma type
  // Σ (x : dom) . b[x]
  data class Sg(val dom: Term, val fam: Fam) : Term

  // element of Pi type
  // λ x . b[x]
  data class Lam(val fam: Fam) : Term

  // element of Sigma type
  // <fst , snd>
  data class Pair(val fst: Term, val snd: Term) : Term

  // function application
  data class App(val lam: Term, val arg: Term) : Term

  // first pair projection
  data class Fst(val pair: Term) : Term

  // second pair projection
  data class Snd(val pair: Term) : Term

  // universe
  data object Univ : Term
}
