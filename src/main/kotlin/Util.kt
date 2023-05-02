package org.yurusanp.smallfontt

// term with a binder, i.e., a family of terms
// x . b
@JvmInline
value class Fam(val b: Term)

@JvmInline
value class Nat(private val inner: Int) {
  init {
    require(inner >= 0)
  }

  fun pred(): Nat = Nat(inner - 1)

  fun isZ(): Boolean = inner == 0
}

// de Bruijn index, starting from the right with 0
typealias Ix = Nat

// de Bruijn level, starting from the left with 0
typealias Lv = Nat

// closure of a term with a binder
// Γ, x : A ⊢ b : B
// the purpose is to avoid actually doing expensive substitution
data class Cls(val env: Env, val fam: Fam)

// for each (z : C) in Γ, a value is in the env
sealed interface Env {
  data object Emp: Env
  data class Ext(val env: Env, val v: Value): Env

  fun proj(ix: Ix): Value = when (this) {
    is Env.Emp -> throw IllegalArgumentException("Got an empty environment!")
    is Env.Ext -> if (ix.isZ()) v else env.proj(ix.pred())
  }
}
