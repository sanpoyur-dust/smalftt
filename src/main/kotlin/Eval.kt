package org.yurusanp.smalftt

// the evaluation part of normalization
fun eval(env: Env, term: Term): Value = when (term) {
  is Term.Var -> env.proj(term.ix)

  is Term.Pi -> {
    val (dom, fam) = term
    val vDom = eval(env, dom)
    Value.Pi(vDom, Cls(env, fam))
  }

  is Term.Sg -> {
    val (dom, fam) = term
    val vDom = eval(env, dom)
    Value.Sg(vDom, Cls(env, fam))
  }

  is Term.Lam -> Value.Lam(Cls(env, term.fam))

  is Term.Pair -> {
    val (fst, snd) = term
    val vFst = eval(env, fst)
    val vSnd = eval(env, snd)
    Value.Pair(vFst, vSnd)
  }

  // we don't do the substitution here since deep substitutions are expensive
  // we only extend the env for later look up

  is Term.App -> {
    val (lam, arg) = term
    val vFn = eval(env, lam)
    val vArg = eval(env, arg)
    app(vFn, vArg)
  }

  is Term.Fst -> {
    val vPair = eval(env, term.pair)
    fst(vPair)
  }

  is Term.Snd -> {
    val vPair = eval(env, term.pair)
    snd(vPair)
  }

  else -> TODO()
}

fun app(vLam: Value, vArg: Value): Value = when (vLam) {
  is Value.Lam -> {
    val (env, fam) = vLam.cls
    eval(Env.Ext(env, vArg), fam.b)
  }

  is Value.Stuck -> {
    val (nLam, pi) = vLam

    // vLam : Π (x : vDom) . b[x]
    if (pi !is Value.Pi) error("Expected a value of Pi type. Got $nLam: $pi")
    val (vDom, cls) = pi

    val nApp = Neutral.App(nLam, vArg, vDom)

    // instantiate the family with the argument
    // [vArg/x] b
    val (env, fam) = cls
    val fiber = eval(Env.Ext(env, vArg), fam.b)

    Value.Stuck(nApp, fiber)
  }

  else -> error("Expected a value of Pi type. Got $vLam")
}

fun fst(vPair: Value): Value = when (vPair) {
  is Value.Pair -> {
    val (vFst, _) = vPair
    vFst
  }

  is Value.Stuck -> {
    val (nPair, sg) = vPair

    // vPair : Σ (x : vDom) . b[x]
    if (sg !is Value.Sg) error("Expected a value of Sigma type. Got $nPair: $sg")
    val (vDom, _) = sg

    val nFst = Neutral.Fst(nPair)

    Value.Stuck(nFst, vDom)
  }

  else -> error("Expected a value of Sigma type. Got $vPair")
}

fun snd(vPair: Value): Value = when (vPair) {
  is Value.Pair -> {
    val (_, vSnd) = vPair
    vSnd
  }

  is Value.Stuck -> {
    val (nPair, sg) = vPair

    // vPair : Σ (x : _) . b[x]
    if (sg !is Value.Sg) error("Expected a value of Sigma type. Got $nPair: $sg")
    val (_, cls) = sg

    val nSnd = Neutral.Snd(nPair)

    // instantiate the family with the first projection
    // [vFst/x] b
    val (env, fam) = cls
    val vFst = fst(vPair)
    val fiber = eval(Env.Ext(env, vFst), fam.b)

    Value.Stuck(nSnd, fiber)
  }

  else -> error("Expected a value of Sigma type. Got $vPair")
}
