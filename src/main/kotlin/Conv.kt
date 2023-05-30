package org.yurusanp.smalftt

// throw if two terms are not definitionally equal (convertible)
data class UnequalException(override val message: String)
  : Exception("Unequal: $message")

fun <T> unequal(lhs: T, rhs: T): Nothing =
  throw UnequalException("lhs $lhs is not equal to rhs $rhs")

// a type-directed conversion algorithm
// we don't do reification since we don't have to
fun conv(len: Int, ty: Value, lhs: Value, rhs: Value) {
  when (ty) {
    is Value.Pi -> {
      val (vDom, cls) = ty

      val fresh = Value.Stuck(Neutral.Var(Lv(len)), vDom)

      val appL = app(lhs, fresh)
      val appR = app(rhs, fresh)
      val fiber = eval(Env.Ext(cls.env, fresh), cls.fam.b)
      conv(len + 1, fiber, appL, appR)
    }

    is Value.Sg -> {
      val (vDom, cls) = ty

      val fstL = fst(lhs)
      val fstR = fst(rhs)
      conv(len, vDom, fstL, fstR)

      val sndL = snd(lhs)
      val sndR = snd(rhs)
      val fiber = eval(Env.Ext(cls.env, fstL), cls.fam.b)
      conv(len + 1, fiber, sndL, sndR)
    }

    // no η-law available!
    else -> {
      when (lhs) {
        is Value.True -> if (rhs !is Value.True) unequal(lhs, rhs)

        is Value.False -> if (rhs !is Value.False) unequal(lhs, rhs)

        is Value.Stuck -> {
          if (rhs !is Value.Stuck) unequal(lhs, rhs)

          val (neuL, tyL) = lhs
          val (neuR, tyR) = rhs
          convTy(len, tyL, tyR)
          convNeu(len, neuL, neuR)
        }

        else -> TODO()
      }
    }
  }
}

fun convTy(len: Int, lhs: Value, rhs: Value) {
  when (lhs) {
    is Value.Pi -> {
      if (rhs !is Value.Pi) unequal(lhs, rhs)

      val (vDomL, clsL) = lhs
      val (_, clsR) = rhs
      convTy(len, lhs.vDom, rhs.vDom)

      val (envL, famL) = clsL
      val (envR, famR) = clsR

      // len would be just outside the current env
      // i.e., the rightmost position after the env extension

      val fresh = Value.Stuck(Neutral.Var(Lv(len)), vDomL)

      val fiberL = eval(Env.Ext(envL, fresh), famL.b)
      val fiberR = eval(Env.Ext(envR, fresh), famR.b)
      convTy(len + 1, fiberL, fiberR)
    }

    is Value.Sg -> {
      // basically the same as Pi

      if (rhs !is Value.Sg) unequal(lhs, rhs)

      val (vDomL, clsL) = lhs
      val (envL, famL) = clsL
      convTy(len, lhs.vDom, rhs.vDom)

      val (_, clsR) = rhs
      val (envR, famR) = clsR

      val fresh = Value.Stuck(Neutral.Var(Lv(len)), vDomL)

      val fiberL = eval(Env.Ext(envL, fresh), famL.b)
      val fiberR = eval(Env.Ext(envR, fresh), famR.b)
      convTy(len + 1, fiberL, fiberR)
    }

    else -> TODO()
  }
}

fun convNeu(len: Int, lhs: Neutral, rhs: Neutral) {
  when (lhs) {
    is Neutral.Var -> {
      if (rhs !is Neutral.Var) unequal(lhs, rhs)

      val (lvL) = lhs
      val (lvR) = rhs
      if (lvL != lvR) unequal(lhs, rhs)
    }

    is Neutral.App -> {
      if (rhs !is Neutral.App) unequal(lhs, rhs)

      val (nLamL, vArgL, vDomL) = lhs
      val (nLamR, vArgR, vDomR) = rhs
      convNeu(len, nLamL, nLamR)
      convTy(len, vDomL, vDomR)
      conv(len, vDomL, vArgL, vArgR)
    }

    is Neutral.Fst -> {
      if (rhs !is Neutral.Fst) unequal(lhs, rhs)

      val (nPairL) = lhs
      val (nPairR) = rhs
      convNeu(len, nPairL, nPairR)
    }

    is Neutral.Snd -> {
      if (rhs !is Neutral.Snd) unequal(lhs, rhs)

      val (nPairL) = lhs
      val (nPairR) = rhs
      convNeu(len, nPairL, nPairR)
    }

    else -> TODO()
  }
}
