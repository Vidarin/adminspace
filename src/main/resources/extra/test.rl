module Cake {
    $open str flavor
    $closed str adjective

    factory () {
        this.adjective = "delicious"
    }

    factory ($str s) {
        this.adjective = s
    }

    const void test () {
        print;"test"
    }

    open void taste ($this) {
        print;"The " + this.flavor + " cake is " + this.adjective + "!"
    }

    module Icing {
        $closed str type = "None :("

        open str getType ($this) {
            return "Icing: " + this.type
        }
    }
}

{
    Cake.test;;
    $Cake cake = Cake;;
    cake.flavor = "German chocolate"
    cake.taste;; // Prints "The German chocolate cake is delicious!"

    $Cake.Icing icing = Cake.Icing;;
    print;icing.getType;;
}