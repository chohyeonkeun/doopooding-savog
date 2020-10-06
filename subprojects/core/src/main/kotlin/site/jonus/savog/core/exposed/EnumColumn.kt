package site.jonus.savog.core.exposed

interface EnumAliasColumn<T> {
    val alias: T
}

interface EnumIntColumn : EnumAliasColumn<Int> {
    val number: Int
    override val alias: Int
        get() = number
}

interface EnumStringColumn : EnumAliasColumn<String>

interface EnumCharColumn : EnumAliasColumn<Char>