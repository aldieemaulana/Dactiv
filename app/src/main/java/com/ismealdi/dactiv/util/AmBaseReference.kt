package com.ismealdi.dactiv.util

import android.os.Parcel
import com.google.firebase.firestore.FirebaseFirestore
import com.ismealdi.dactiv.structure.*
import java.util.*

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

fun FirebaseFirestore.rapat() = collection(Rapat.Path.Name)
fun FirebaseFirestore.rapat(uid: String) = collection(Rapat.Path.Name).document(uid)

fun FirebaseFirestore.kegiatan() = collection(Kegiatan.Path.Name)
fun FirebaseFirestore.kegiatan(uid: String) = collection(Kegiatan.Path.Name).document(uid)

fun FirebaseFirestore.message() = collection(Message.Path.Name)
fun FirebaseFirestore.message(uid: String) = collection(Message.Path.Name).document(uid)

fun FirebaseFirestore.alert() = collection(Alert.Path.Name)
fun FirebaseFirestore.alert(uid: String) = collection(Alert.Path.Name).document(uid)

fun Parcel.writeDate(date: Date?) = writeLong(date?.time ?: -1)
fun Parcel.readDate(): Date? = if (readLong() != -1L) Date(readLong()) else null