extends TextureButton

var rng = RandomNumberGenerator.new()
var chislo
var nowPlay = false
var newNumber = false
var firstNumber = true
var musicOff = false
var randomtTimeForMusic
var my_random_number
# Called when the node enters the scene tree for the first time.
func _ready():
	pass # Replace with function body.
	connect("button_down",self,"_playfucingmusic")
# Called every frame. 'delta' is the elapsed time since the previous frame.
#func _process(delta):
#	pass

func _nextfuckingmusic():
	_onfuckingmusic()

func _onfuckingmusic():
	rng.randomize()
	randomtTimeForMusic = rng.randf_range(0.0,30.0)
	if(nowPlay == true):
		get_child(chislo).stop()
		nowPlay = false
	while !newNumber:
		my_random_number = rng.randi_range(0, 4)
		if(my_random_number!=chislo):
			newNumber = true
			chislo = my_random_number
		elif(firstNumber):
			chislo = my_random_number
			newNumber = true
	firstNumber = false
	newNumber = false
	nowPlay = true
	get_child(my_random_number).play(randomtTimeForMusic)


func _playfucingmusic():
	if(!musicOff):
		_onfuckingmusic()
		musicOff = true
	else:
		get_child(chislo).stop()
		musicOff = false
	
