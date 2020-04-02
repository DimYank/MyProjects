extends TextureRect

func _ready():
	hide()
	$TextureButton.connect("button_down", self, "close")

func close():
	hide()