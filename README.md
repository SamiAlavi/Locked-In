# **Locked In**

**Locked In** is a minimalistic Android app designed to help you focus by **locking your device for a set period of time**. Once activated, the app enforces a strict lock, preventing the user from leaving the app until the timer expires.

This project is intended for **personal or development use** — no device-owner privileges are required, and it can be **uninstalled at any time**.

---

## **Key Features**

* Set a **custom lock duration** (default: 20 minutes)
* **Strict Lock Task enforcement**: Home and Recent apps are blocked
* **Immersive black screen** for minimal distraction
* **Keyboard and input hidden** during lock
* **Static foreground service notification** (no timer info, minimal battery usage)
* **Screen allowed to lock / dim naturally** to conserve battery
* Works on **Android 13+ / API 36** without extra permissions

---

## **Use Case**

* Stay focused during work or study sessions
* Prevent accidental app or device usage during lock
* Test strict lock behavior in dev mode

---

## **Notes**

* First-time users may see the Android **“screen pinning” info prompt**. You must accept it to enforce the lock.
* No emergency exit is provided — the app fully locks the device until the timer ends.
* Battery-friendly design: black screen + dimmed brightness, minimal CPU usage.

---

## **Tech Stack**

* **Kotlin**
* Android **API 36** (Android 13+)
* Foreground service + Lock Task mode

---
