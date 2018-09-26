package com.wjaronski.bricklistapp.model.Klocek

import android.graphics.Bitmap
import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Klocek(
        var id: Int,
        var TypeID: String,
        var ItemID: String,
        var qty: Int,
        var qtyInStore: Int,
        var ColorID: Int,
        var Color: Int,
        var extra: String,
        var alternate: String,
        var inventoryId: Int?,
        var Code: String,
        var UniqueCode: Int,
        var Desc: String?,
        var Image: String?,
        var Condition: String
) : Parcelable {
    var changed = false

    var image: Bitmap? = null

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
        changed = parcel.readByte() != 0.toByte()
    }

    fun getQty(): CharSequence? {
        return "$qtyInStore/$qty"
    }

    fun getDesc(): CharSequence? {
        if (Desc == "") {
            return "Brak opisu, kod: \"$ItemID\", kolorId: \"$Color\""
        } else {
            return "$Desc"
        }


    }

    constructor() : this(0, "", "", 0, 0, 0, 0, "", "", 0, "", 0, "", "","")


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(TypeID)
        parcel.writeString(ItemID)
        parcel.writeInt(qty)
        parcel.writeInt(qtyInStore)
        parcel.writeInt(ColorID)
        parcel.writeInt(Color)
        parcel.writeString(extra)
        parcel.writeString(alternate)
        parcel.writeValue(inventoryId)
        parcel.writeString(Code)
        parcel.writeInt(UniqueCode)
        parcel.writeString(Desc)
        parcel.writeString(Image)
        parcel.writeString(Condition)
        parcel.writeByte(if (changed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Klocek> {
        val TABLE_NAME = "InventoriesParts"
        val ID = "id"
        val INVENTORY_ID = "InventoryID"
        val TYPE_ID = "TypeID"
        val ITEM_ID = "ItemID"
        val QUANTITY_IN_SET = "QuantityInSet"
        val QUANTITY_IN_STORE = "QuantityInStore"
        val COLOR_ID = "ColorID"
        val EXTRA = "Extra"

        override fun createFromParcel(parcel: Parcel): Klocek {
            return Klocek(parcel)
        }

        override fun newArray(size: Int): Array<Klocek?> {
            return arrayOfNulls(size)
        }
    }

}
