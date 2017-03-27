# Yet Another ScalaJS Bootplate

## Purpose

This digression on Otto Chrons' excellent [Scala.js SPA-tutorial](https://github.com/ochrons/scalajs-spa-tutorial) is meant to be a starting point for future [Scala.js](https://www.scala-js.org/) projects with [database access](http://slick.lightbend.com/) and a separate [React component set](https://github.com/chandu0101/scalajs-react-components), while keeping things organized around his [Diode](https://github.com/suzaku-io/diode) library.

## How to use

[sbt](http://www.scala-sbt.org/), [npm](https://www.npmjs.com/) and [gulp](http://gulpjs.com/) must be installed prior to launching the project with
`sbt run`.

[Vagrant](https://www.vagrantup.com/) can also be used to provide homogenous developement environments :
```
vagrant up
vagrant ssh
cd /vagrant
sbt run
```

## Documentation

Updated documentation for the original project can be found at <https://ochrons.github.io/scalajs-spa-tutorial/>.

## Differences with the original

* Embryonary database support through [slick](http://slick.lightbend.com/) generated code.
* Javascript dependencies managed with [scalajs-bundler](https://scalacenter.github.io/scalajs-bundler/).
* Inner bootstrap components  replaced with [chandu0101's ScalaJS react components](https://github.com/chandu0101/scalajs-react-components).
* TODOs page has been removed.
