extends Node

const cut_scenes_path = "res://cut_scenes/"
onready var text_label = $bg/text/textbox/MarginContainer/textLabel
onready var bg = $bg

var scene_lines = []
var progress = -1

var typing = false
var breakTyping = false

func _ready():
	game_state.add_curtains("open")
	set_scene()

func _input(event):
	if event.is_action_pressed("cut_progress"):
		if typing:
			breakTyping = true
		else:
			progress_scene()

func set_scene():
	var file = File.new()
	file.open(cut_scenes_path + game_state.cut_scene_name + ".res", File.READ)
	print(game_state.cut_scene_name)
	while !file.eof_reached():
		var string = file.get_line()
		if string.begins_with("!") || string.begins_with("#") || string.begins_with("?") :
			scene_lines.append(string)
		else:
			scene_lines[scene_lines.size() - 1] += "\n" + string
	file.close()
	game_state.cut_scene_name = ""
	progress_scene()

func progress_scene():
	progress += 1
	if progress >= scene_lines.size():
		game_state.add_curtains("close").connect("closed", self, "next_scene")
		return
		
	if scene_lines[progress].begins_with("!"):
		bg.texture = load("res://textures/bg/" + scene_lines[progress].lstrip("!"))
		progress_scene()
	elif scene_lines[progress].begins_with("?"):
		$AudioStreamPlayer.stream = load("res://backgroundmusic/"+scene_lines[progress].lstrip("?"))
		$AudioStreamPlayer.play()
		progress_scene()
	else:
		print_text(scene_lines[progress].lstrip("#"))
		
func print_text(text):
	text_label.text = ""
	typing = true
	for c in text:
		text_label.text += c
		if breakTyping:
			text_label.text = text
			breakTyping = false
			break
		yield(get_tree().create_timer(0.06), "timeout")
	typing = false
	
func next_scene():
	get_tree().change_scene("scenes/" + game_state.after_cut_scene + ".tscn")
	game_state.after_cut_scene = ""