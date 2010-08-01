package test

import org.specs._

import _root_.util.Config

class TestProperties extends Specification {

  "foobar" in {
    Config.get("foo") must be equalTo("bar")
  }
}

// vim: set ts=2 sw=2 et:
