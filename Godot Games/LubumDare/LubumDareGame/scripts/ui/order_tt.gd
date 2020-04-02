extends TextureRect

onready var address_label = $elements/from
onready var price_label = $elements/price
onready var accept_but = $elements/accept_but

var order

func _ready():
	set_tt()

func set_tt():
	address_label.text = "Адрес: " + order.address
	price_label.text = "Цена: " + String(order.price) + " p"
	accept_but.connect("button_down", self, "accept_order")

func accept_order():
	game_state.accept_order(order)
	get_parent().hide_point()