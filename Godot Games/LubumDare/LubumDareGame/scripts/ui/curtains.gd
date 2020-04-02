extends TextureRect

signal closed

func open():
	show()
	$AnimationPlayer.play("open")
	$AnimationPlayer.connect("animation_finished", self, "delete")

func close():
	show()
	$AnimationPlayer.play("close")
	$AnimationPlayer.connect("animation_finished", self, "closed")

func closed(lul):
	emit_signal("closed")

func delete(lul):
	queue_free()