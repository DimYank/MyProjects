extends Node

const message = preload("res://scenes/ui/message.tscn")
const choice = preload("res://scenes/ui/choice.tscn")
const msg1 = preload("res://textures/ui/mesg.png")
const msg2 = preload("res://textures/ui/msg2.png")
const path = "res://dialogs/"
onready var message_box = $bg/phone/ScrollContainer/VBoxContainer

var dialogDict

func _ready():
	game_state.add_curtains("open")
	$bg/car/AnimationPlayer.play("wheelAnim")
	set_dialog()

func set_dialog():
	var file = File.new()
	file.open(path + game_state.dialog_name + ".json", File.READ)
	dialogDict = JSON.parse(file.get_as_text()).result
	print(dialogDict)
	print(JSON.parse(file.get_as_text()).error_string)
	print(JSON.parse(file.get_as_text()).error_line)
	start_messaging()
	game_state.dialog_name = ""

func start_messaging():
	var inst_m
	var inst_c
	for child in message_box.get_children():
		child.queue_free()
		
	for dict in dialogDict:
		yield(get_tree().create_timer(1), "timeout")
		inst_m = message.instance()
		if dict["type"] == "mes":
			inst_m = message.instance()
			if dict["cli"]:
				message_box.add_child(inst_m)
				inst_m.get_child(0).get_child(0).text = dict["cli"]
				inst_m.set("custom_constants/margin_left", 70)
				inst_m.get_child(0).texture = msg1
			else:
				message_box.add_child(inst_m)
				inst_m.get_child(0).get_child(0).text = dict["lub"]
				inst_m.set("custom_constants/margin_right", 70)
				inst_m.get_child(0).texture = msg2
		elif dict["type"] == "cho":
			inst_c = choice.instance()
			inst_c.get_child(0).texture = msg2
			message_box.add_child(inst_c)
			inst_c.get_node("choice/choice1/mesage").text = "> " + dict["1"]
			inst_c.get_node("choice/choice2/mesage").text = "> " + dict["2"]
			inst_c.get_node("choice/choice1").connect("button_down", self, "apply_points", [dict["give"], inst_c.get_node("choice/choice2")])
			inst_c.get_node("choice/choice2").connect("button_down", self, "apply_points", [-dict["take"], inst_c.get_node("choice/choice1")])

func apply_points(points, but):
	if !dialogDict[dialogDict.size()-1]["name"]:
		game_state.add_curtains("close").connect("closed", self, "to_map")
		return
	but.hide()
	game_state.client_points += points
	game_state.ride_name = dialogDict[dialogDict.size()-1]["name"]
	game_state.add_curtains("close").connect("closed", self, "next_scene")

func next_scene():
	get_tree().change_scene("res://scenes/taxi_ride.tscn")

func to_map():
	get_tree().change_scene("res://scenes/map.tscn")