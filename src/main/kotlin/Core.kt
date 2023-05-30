package org.yurusanp.smalftt

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

  // type of types
  // Type
  data object Type : Term

  // Unit type and its constructor and eliminator
  // () : Unit
  data object UnitTy : Term
  data object Unit : Term
  data class UnitInd(val scrut: Term, val mot: Fam, val caseU: Term) : Term

  // Bool type and its constructors
  // True, False : Bool
  data object BoolTy : Term
  data object True : Term
  data object False : Term
  data class BoolInd(val scrut: Term, val mot: Fam, val caseT: Term, val caseF: Term) : Term
}
