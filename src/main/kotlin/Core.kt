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

  // constructor for Pi type
  // λ x . b[x]
  data class Lam(val fam: Fam) : Term

  // constructor for Sigma type
  // <fst , snd>
  data class Pair(val fst: Term, val snd: Term) : Term

  // function application, eliminator for Pi type
  data class App(val lam: Term, val arg: Term) : Term

  // first pair projection, first eliminator for Sigma type
  data class Fst(val pair: Term) : Term

  // second pair projection, second eliminator for Sigma type
  data class Snd(val pair: Term) : Term

  // universe
  data object Univ : Term
}
