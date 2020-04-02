extends TextureButton

export(String) var order_address
export(int) var order_price
export(String) var order_dialog_name
const order_sc = preload("res://scenes/ui/order.tscn")

#onready var canvas = get_parent().get_parent().get_node("CanvasLayer")
var order_inst

func _ready():
	connect("mouse_entered", self, "show_order")
	connect("mouse_exited", self, "hide_order")
	inst_order()
	
func inst_order():
	var order = load("res://scripts/classes/order.gd").new()
	order.set_values(order_address, order_price)
	order_inst = order_sc.instance()
	var position = Vector2(35, -order_inst.rect_size.y + 25)
#	if position.x + order_inst.rect_size.x > 1920:
#		order_inst.rect_scale.x = -1
#		order_inst.get_child(0).position.x -= order_inst.rect_size.x
#	if position.y < 0:
#		order_inst.rect_scale.y = -1
#		order_inst.get_child(0).position.y -= order_inst.rect_size.y
	order_inst.rect_position = position
	order_inst.order = order
	add_child(order_inst)
	hide_order()
	
func show_order():
	order_inst.show()
	
func hide_order():
	order_inst.hide()

func hide_point():
	game_state.add_curtains("close").connect("closed", self, "to_taxi")
	hide()

func to_taxi():
	game_state.dialog_name = order_dialog_name
	get_tree().change_scene("res://scenes/taxi_dialog.tscn")