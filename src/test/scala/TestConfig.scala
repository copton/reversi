package test

import org.scalatest.FunSuite

import _root_.util.Config

import java.net.URLClassLoader


class TestConfig extends FunSuite {

  test("Config") {
    val key = "foo"
    val value = "bar"
    val result = Config.get(key)
    assert(result == value)
  }
}

// vim: set ts=2 sw=2 et:
