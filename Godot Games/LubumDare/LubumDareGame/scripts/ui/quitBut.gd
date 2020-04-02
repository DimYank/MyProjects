extends TextureButton

func _ready():
	connect("button_down", self, "quit")

func quit():
	get_tree().quit()