extends Node

onready var new_game_but = $buttons/newGameBut
onready var continue_but = $buttons/continueBut
onready var quit_but = $buttons/quitBut

func _ready():
	new_game_but.connect("button_down", self, "on_new_game_but")
	continue_but.connect("button_down", self, "on_continue_but")
	quit_but.connect("button_down", self, "on_quit_but")

func on_new_game_but():
	game_state.add_curtains("close").connect("closed", self, "start_new_game")
	
func on_continue_but():
	pass

func on_quit_but():
	get_tree().quit()

func start_new_game():
	game_state.new_game = true
	game_state.cut_scene_name = "test"
	game_state.after_cut_scene = "map"
	get_tree().change_scene("res://scenes/cut_scene.tscn")