package promise.app.mock

import android.os.Parcel
import android.os.Parcelable
import dev4vin.promise.model.SModel

data class MockObject(val int: Int, val string: String): SModel() {

  constructor(parcel: Parcel) : this(
      parcel.readInt(),
      parcel.readString()!!)

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    super.writeToParcel(parcel, flags)
    parcel.writeInt(int)
    parcel.writeString(string)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<MockObject> {
    override fun createFromParcel(parcel: Parcel): MockObject {
      return MockObject(parcel)
    }

    override fun newArray(size: Int): Array<MockObject?> {
      return arrayOfNulls(size)
    }
  }
}