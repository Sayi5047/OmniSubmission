import com.google.gson.annotations.SerializedName



data class Categories (

	@SerializedName("name") val name : String,
	@SerializedName("videos") val videos : List<Videos>
)