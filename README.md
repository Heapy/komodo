# Komodo - Kotlin Modular Framework

[![Build Status](https://travis-ci.org/Heapy/komodo.svg?branch=master)](https://travis-ci.org/Heapy/komodo) [![Join the chat at https://gitter.im/Heapy/komodo](https://badges.gitter.im/Heapy/komodo.svg)](https://gitter.im/Heapy/komodo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![Logo](./logo.png)

## Goals

1. Simple and fast replacement for Spring Boot Starters. That mean that we use kpress and bootique inspired modular system based on Java SPI.
2. No Java. Maybe we can use this project in other Kotlin backends such as KotlinJs and Kotlin Native. To aim that, we shouldn't rely on Spring DI too much, but use it as one of possible DI options.

## Modules

* komodo - Main komodo package. Assemble all modules, and provide best experience. For now - it's just `komodo-light` 
* komodo-app
* komodo-args
* komodo-common
* komodo-config
* komodo-console
* komodo-core
* komodo-di
* komodo-light
* komodo-samples
* komodo-scripting


## Why Spring DI (spring-context)?

For now it will be simple solution for our DI backend, but in future we should be able to replace it komodo core and basic modules.

## Issues

Issues located in [YouTrack](https://heapy.myjetbrains.com/youtrack/issues/KOMODO) since github issues unsound, but you can submit bugs and requests in github issues.

## LICENCE

Komodo is Open Source software released under the Apache 2.0 license.

