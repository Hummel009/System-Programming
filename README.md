[![Code Smells][code_smells_badge]][code_smells_link]
[![Maintainability Rating][maintainability_rating_badge]][maintainability_rating_link]
[![Security Rating][security_rating_badge]][security_rating_link]
[![Bugs][bugs_badge]][bugs_link]
[![Vulnerabilities][vulnerabilities_badge]][vulnerabilities_link]
[![Duplicated Lines (%)][duplicated_lines_density_badge]][duplicated_lines_density_link]
[![Reliability Rating][reliability_rating_badge]][reliability_rating_link]
[![Quality Gate Status][quality_gate_status_badge]][quality_gate_status_link]
[![Technical Debt][technical_debt_badge]][technical_debt_link]
[![Lines of Code][lines_of_code_badge]][lines_of_code_link]

Мои лабораторные работы для BSUIR/БГУИР (белорусский государственный университет информатики и радиоэлектроники).

Предмет - SP/СП (системное программирование).

## Условия

### Курсовая работа

Тема - "Устройство для записи и визуализации звука". Проект состоит из двух отдельных программ, одна вызывает другую
через графический интерфейс и передаёт в неё параметры. Запись звука идёт через WinAPI, визуализация отрисовывается на
JavaFX. Обе части работы реализованы на языке Kotlin, но одна из них (запись звука) с технологией компиляции Native.

### Лабораторная работа 1

Изучение событийной архитектуры Windows-приложений, механизмы обработки сообщений, механизмы перерисовки окна.

* Разработать программу, позволяющую передвигать с помощью клавиатуры и мыши спрайт (или геометрическую фигуру) внутри
  рабочей области окна.
* Обеспечить работу колёсика мыши (движение по вертикали, если shift – по горизонтали).
* Придать спрайту движение с отскоком от границ окна.
* Задействовать хотя бы одну горячую клавишу (ctrl + <smth>) с использованием таблиц акселерации.

### Лабораторная работа 2

Изучение вывода текста и шрифтов.

* Разработать программу, которая вписывает в окно текстовую таблицу N строк на M столбцов таким образом, что все столбцы
  таблицы равномерно распределяются по ширине окна, а высота строк таблицы подбирается таким образом, чтобы вместить
  текст каждой ячейки.
* При изменении размеров окна таблица пересчитывается и перерисовывается.
* Вписать текст в окружность так, чтобы он выглядел, как на печати.

### Лабораторная работа 3

Изучение создания использования динамических загружаемых библиотек DLL.

* Разработать программу, которая выполняет Runtime импорт DLL и вызывает её функцию.
* Разработать программу, которая выполняет Loadtime импорт DLL и вызывает её функцию.
* Разработать DLL с функцией поиска заданной строки по всей виртуальной памяти и замены этой строки на другую.
* Разработать программу, которая внедряет DLL в заданный процесс и вызывает её функцию.

### Лабораторная работа 4

Изучение работы с реестром. Разработать программу, которая демонстрирует следующий функционал:

* Создание ключа, открытие ключа, закрытие ключа, удаление ключа, замена содержимого ключа.
* Вывод флагов ключа.
* Отслеживание изменения ключа.

### Лабораторная работа 5

Изучение, создание и использование потоков и механизмов синхронизации.

* Разработать очередь заданий, в которой несколько потоков могут вставлять элементы атомарно.
* Разработать обработчик этой очереди, который извлекает из неё задания и раздаёт заданному количеству потоков.
* Разработать программу, которая использует очередь заданий и обработчик очереди для сортировки строк в .txt файле:
    * Входной поток читает файл в память, нарезает его на части и создаёт несколько заданий на сортировку по числу
      сортирующих потоков.
    * Входной поток помещает их в очередь заданий.
    * Сортирующие потоки извлекают задание, сортируют свои части файла, отдают результаты выходному потоку.
    * Выходной поток ждёт все сортирующие части и мержит их методом сортирующего слияния.

## Приложения

В папке docs лежит официальная презентация, которую показывали на лекциях.

<!----------------------------------------------------------------------------->

[code_smells_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=code_smells

[code_smells_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[maintainability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=sqale_rating

[maintainability_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[security_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=security_rating

[security_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[bugs_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=bugs

[bugs_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[vulnerabilities_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=vulnerabilities

[vulnerabilities_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[duplicated_lines_density_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=duplicated_lines_density

[duplicated_lines_density_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[reliability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=reliability_rating

[reliability_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[quality_gate_status_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=alert_status

[quality_gate_status_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[technical_debt_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=sqale_index

[technical_debt_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming

[lines_of_code_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_System-Programming&metric=ncloc

[lines_of_code_link]: https://sonarcloud.io/summary/overall?id=Hummel009_System-Programming
