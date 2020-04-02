extends Node

const curtains_sc = preload("res://scenes/misc/curtains.tscn")

#process-relate
var new_game = false
var cut_scene_name = ""
var after_cut_scene = ""
var dialog_name = ""
var ride_name = ""

#player-related
var accepted_orders = []
var money = 0

#ride-related
var client_points setget cliet_points_set 

signal accepted
signal mood

func _ready():
	client_points = 0

func cliet_points_set(value):
	client_points = value
	if client_points > 100:
		client_points=100
	elif client_points < 0:
		client_points = 0
	emit_signal("mood")

func accept_order(order):
	accepted_orders.append(order)
	emit_signal("accepted")

func add_curtains(for_what):
	var inst = curtains_sc.instance()
	if for_what == "close":
		inst.close()
	elif for_what == "open":
		inst.modulate = Color(1,1,1,1)
		inst.open()
	get_tree().current_scene.add_child(inst)
	return inst