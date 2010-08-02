package test

import org.scalatest.FunSuite

import _root_.util.Config

import java.net.URLClassLoader


class TestConfig extends FunSuite {

  test("Config") {

        val sysClassLoader = ClassLoader.getSystemClassLoader();
        val urls = sysClassLoader.asInstanceOf[URLClassLoader].getURLs();

        for(url <- urls) 
        {
            System.out.println(url.getFile());
        }       

    val key = "foo"
    val value = "bar"
    val result = Config.get(key)
    assert(result == value)
  }
}

// vim: set ts=2 sw=2 et:
