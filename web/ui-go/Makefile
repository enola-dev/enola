PROTO_DIR := ../../core/lib/src/main/java/dev/enola/core
PROTO_FILE := enola_core.proto
GO_OUT := generated

.PHONY: all
all: generate

.PHONY: generate
generate:
	@echo "Generating Go code from protobuf..."
	protoc --proto_path=$(PROTO_DIR) --go_out=$(GO_OUT) $(PROTO_DIR)/$(PROTO_FILE)
	protoc --proto_path=$(PROTO_DIR) --go-grpc_out=$(GO_OUT) $(PROTO_DIR)/$(PROTO_FILE)
	@echo "Go code generated successfully."

.PHONY: clean
clean:
	@echo "Cleaning generated Go code..."
	rm -rf $(GO_OUT)
	@echo "Clean completed."
