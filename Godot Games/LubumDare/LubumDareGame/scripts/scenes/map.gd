extends Node

onready var points = $bg
onready var money = $moneyLabel

func _ready():
	game_state.add_curtains("open")
	if game_state.accepted_orders.size() == 4 && game_state.money < 3000:
		get_tree().change_scene("res://scenes/game_over_2.tscn")
		return
	check_points()
	if game_state.new_game:
		new_game()
	money.text = "Деньги:"+String(game_state.money)+"p"
	if game_state.money < 3000:
		$buyDocsBut.disabled = true
	$buyDocsBut.connect("button_down", self, "buy_docs")
	
	

func check_points():
	for point in points.get_children():
		for order in  game_state.accepted_orders:
			if point.order_address == order.address:
				point.hide()
				print("hidden")

func new_game():
	game_state.new_game = false
	$wind.show()

func buy_docs():
	game_state.add_curtains("close").connect("closed", self, "game_over")

func game_over():
	get_tree().change_scene("res://scenes/game_over.tscn")