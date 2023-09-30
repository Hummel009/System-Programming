CXX       := g++
CXX_FLAGS := -std=c++17 -ggdb

LD_FLAGS  := -luser32 -lgdi32

BIN        := bin
SRC        := src
BUILD      := build
INCLUDE    := -Iinclude

LIBRARIES   :=
EXECUTABLE  := main

SRCS := $(wildcard $(SRC)/*.cpp)
OBJS := $(SRCS:$(SRC)/%.cpp=$(BUILD)/%.o)

all: $(BIN)/$(EXECUTABLE)

run: clean all
	clear
	./$(BIN)/$(EXECUTABLE)

$(BIN)/$(EXECUTABLE): $(OBJS)
	$(CXX) $(CXX_FLAGS) $(INCLUDE) $^ -o $@ $(LIBRARIES) $(LD_FLAGS)

$(BUILD)/%.o: $(SRC)/%.cpp
	$(CXX) $(CXX_FLAGS) $(INCLUDE) -c $< -o $@ $(LIBRARIES) $(LD_FLAGS)

clean:
	-rm -rf $(BUILD)/*
	-rm $(BIN)/$(EXECUTABLE)