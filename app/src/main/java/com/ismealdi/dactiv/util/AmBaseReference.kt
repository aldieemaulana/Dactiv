package com.ismealdi.dactiv.util

import com.google.firebase.firestore.FirebaseFirestore
import com.ismealdi.dactiv.listener.*

/**
 * Created by Al on 17/10/2018
 */

fun FirebaseFirestore.golongan() = collection(Golongan.Path.Name)
fun FirebaseFirestore.golongan(id: String) = collection(Golongan.Path.Name).document(id)

fun FirebaseFirestore.jabatan() = collection(Jabatan.Path.Name)
fun FirebaseFirestore.jabatan(id: String) = collection(Jabatan.Path.Name).document(id)

fun FirebaseFirestore.satker() = collection(Satker.Path.Name)
fun FirebaseFirestore.satker(id: String) = collection(Satker.Path.Name).document(id)

fun FirebaseFirestore.user() = collection(User.Path.Name)
fun FirebaseFirestore.user(uid: String) = collection(User.Path.Name).document(uid)