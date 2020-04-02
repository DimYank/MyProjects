extends Node

const path = "res://rides/"
const action_sc = preload("res://scenes/ui/action.tscn")

onready var text_box = $bg/text/textbox/MarginContainer/textLabel
onready var action_box = $bg/actions
onready var passPop = $bg/passanger
onready var driverPop = $bg/driver
onready var moodBar = $bg/stisfactionBar
onready var rewardPanel = $rewards
onready var time_label = $bg/time

var rideDic
var actions = []
var driver_line
var client_line
var typing
var breakTyping
var driverLinePrinted = false
var clientLinePrinted = false
var time = .0
var end = false

func _ready():
	game_state.add_curtains("open")
	$bg/car/AnimationPlayer.play("wheelAnim")
	passPop.hide()
	driverPop.hide()
	set_scene()
	set_mood_bar()
	game_state.connect("mood", self, "set_mood_bar")
	rewardPanel.hide()

func _process(delta):
	if end:
		return
	time+=delta
	time_label.text = "Время " + String(int(time/60))+":"+String(int(time)%60)+"/2:00"
	if time > 120:
		time = 120
		reward()
		
func _input(event):
	if event.is_action_pressed("cut_progress"):
		if typing:
			breakTyping = true
		elif driver_line:
			print_lines()

func set_scene():
	text_box.text = "Начните уже делать что-ниубдь..."
	var file = File.new()
	print(file.open(path + game_state.ride_name + ".json", File.READ))
	
	print(JSON.parse(file.get_as_text()).result)
	print(JSON.parse(file.get_as_text()).error_string)
	print(JSON.parse(file.get_as_text()).error_line)
	init_actions(JSON.parse(file.get_as_text()).result)
	
	
func init_actions(rideDic):
	for key in rideDic:
		actions.append(rideDic[key])
	set_actions()

func set_actions():
	for child in action_box.get_children():
		child.queue_free()
	for action in actions:
		var inst = action_sc.instance()
		action_box.add_child(inst)
		inst.get_child(0).text = action["tip"]
		inst.connect("button_down", self, "on_action_pressed", [action])
	action_box.show()

func on_action_pressed(actDic):
	if !actDic["next"]:
		reward()
		return
	actions[actions.find(actDic)] = actDic["next"]
	driver_line = actDic["driver"]
	client_line = actDic["client"]
	print_lines()
	game_state.client_points += actDic["points"]

func print_lines():
	action_box.hide()
	if !driverLinePrinted:
		driverPop.show()
		passPop.hide()
		print_text(driver_line)
		driverLinePrinted = true
	elif !clientLinePrinted:
		driverPop.hide()
		passPop.show()
		print_text(client_line)
		clientLinePrinted = true
	else:
		driver_line = null
		driverLinePrinted = false
		clientLinePrinted = false
		set_actions()

func print_text(text):
	text_box.text = ""
	typing = true
	for c in text:
		text_box.text += c
		if breakTyping:
			text_box.text = text
			breakTyping = false
			break
		yield(get_tree().create_timer(0.06), "timeout")
	typing = false
	if clientLinePrinted:
		print_lines()

func set_mood_bar():
	print(game_state.client_points)
	moodBar.value = game_state.client_points/100

func reward():
	end = true
	var c
	if game_state.client_points < 25:
		c = 1
	elif game_state.client_points < 50:
		c = 2
	elif game_state.client_points < 100:
		c = 3
	elif game_state.client_points >= 100:
		c = 4
	game_state.money += game_state.accepted_orders[game_state.accepted_orders.size() - 1].price * c;
	action_box.hide()
	rewardPanel.show()
	rewardPanel.get_child(0).text="Вы заработали\n"+String(game_state.accepted_orders[game_state.accepted_orders.size() - 1].price * c)+" р"
	rewardPanel.get_child(2).play("drop")
	game_state.client_points = 0
	rewardPanel.get_child(1).connect("button_down",self,"finish")

func finish():
	game_state.add_curtains("close").connect("closed",self,"to_map")
	
func to_map():
	get_tree().change_scene("res://scenes/map.tscn")