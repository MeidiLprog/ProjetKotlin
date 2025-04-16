package com.example.amioupas

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import io.realm.kotlin.types.RealmUUID

// 📌 Modèle Realm pour stockage local (Avec ID unique automatique)
open class AmiiboEntity : RealmObject {
    @PrimaryKey
    var id: String = RealmUUID.random().toString() // ✅ ID unique automatique
    var name: String = ""
    var image: String = ""
    var gameSeries: String = ""

}
