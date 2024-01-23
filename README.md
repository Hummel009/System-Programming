[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=code_smells)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=bugs)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=duplicated_lines_density)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=sqale_index)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Hummel009_UN-ION-Discord-Bot&metric=ncloc)](https://sonarcloud.io/summary/overall?id=Hummel009_UN-ION-Discord-Bot)

Мои лабораторные работы для BSUIR/БГУИР (белорусский государственный университет информатики и радиоэлектроники).

Предмет - SP/СП (системное программирование).

## Общая информация

Этот репозиторий - проект Gradle, который должен быть открыт через IntelliJ IDEA.

| Технология                             | Версия |
|----------------------------------------|--------|
| Система автоматической сборки Gradle   | 8.5    |
| Java, используемая для запуска Gradle  | 11+    |
| Java, используемая для сборки проекта  | 11+    |
| Java, используемая для запуска проекта | 11+    |
| Kotlin                                 | 1.9.22 |
| Стандарт C++                           | C++23  |
| Компилятор GCC для C/C++               | 13.2.0 |

> [!IMPORTANT]  
> AppLab3 - это набор папок, не предназначенных для запуска из среды программирования. Каждая подпапка содержит в себе решение одного пункта задания на языке С++. Программы запускаются bat-файлами при условии, что на компьютере установлен и сконфигурирован компилятор GCC.

## Установка

Установка моих проектов Gradle и основы работы с ними примерно одинаковы, так что
смотрите [общую инструкцию](https://github.com/Hummel009/The-Rings-of-Power#readme).

## Условия

### Лабораторная работа 1

Изучение событийной архитектуры Windows-приложений, механизмы обработки сообщений, механизмы перерисовки окна.

* Разработать программу, позволяющую передвигать с помощью клавиатуры и мыши спрайт (или геометрическую фигуру) внутри рабочей области окна.
* Обеспечить работу колёсика мыши (движение по вертикали, если shift – по горизонтали).
* Придать спрайту движение с отскоком от границ окна.
* Задействовать хотя бы одну горячую клавишу (ctrl + <smth>) с использованием таблиц акселерации.

### Лабораторная работа 2

Изучение вывода текста и шрифтов.

* Разработать программу, которая вписывает в окно текстовую таблицу N строк на M столбцов таким образом, что все столбцы таблицы равномерно распределяются по ширине окна, а высота строк таблицы подбирается таким образом, чтобы вместить текст каждой ячейки.
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
  * Входной поток читает файл в память, нарезает его на части и создаёт несколько заданий на сортировку по числу сортирующих потоков.
  * Входной поток помещает их в очередь заданий.
  * Сортирующие потоки извлекают задание, сортируют свои части файла, отдают результаты выходному потоку.
  * Выходной поток ждёт все сортирующие части и мержит их методом сортирующего слияния.

### Курсовая работа

Тема - "Устройство для записи и визуализации звука". Проект состоит из двух отдельных программ, одна вызывает другую через графический интерфейс и передаёт в неё параметры. Запись звука идёт через WinAPI, визуализация отрисовывается на JavaFX. Обе части работы реализованы на языке Kotlin, но одна из них (запись звука) с технологией компиляции Native.
